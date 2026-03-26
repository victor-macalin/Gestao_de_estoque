/* ─────────────── UTILS ─────────────── */
const API = '';

function getToken() { return localStorage.getItem('token'); }
function setToken(t) { localStorage.setItem('token', t); }
function clearAuth() { localStorage.removeItem('token'); localStorage.removeItem('user'); }

function authHeaders(extra = {}) {
  return { 'Content-Type': 'application/json', 'Authorization': `Bearer ${getToken()}`, ...extra };
}

async function api(method, path, body) {
  const opts = { method, headers: authHeaders() };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API + path, opts);
  if (res.status === 401) { clearAuth(); location.href = '/login'; return; }
  if (res.status === 204 || res.status === 200 && res.headers.get('content-length') === '0') return null;
  const text = await res.text();
  if (!text) return null;
  const data = JSON.parse(text);
  if (!res.ok) throw new Error(data.message || `Erro ${res.status}`);
  return data;
}

function toast(msg, type = 'ok') {
  const el = document.getElementById('toast');
  if (!el) return;
  el.textContent = msg;
  el.className = `show ${type}`;
  setTimeout(() => el.className = '', 3000);
}

function guardAuth() {
  if (!getToken()) { location.href = '/login'; }
}

function setLoading(btn, on) {
  if (!btn) return;
  if (on) {
    btn.dataset.orig = btn.innerHTML;
    btn.innerHTML = '<span class="spinner"></span>';
    btn.disabled = true;
  } else {
    btn.innerHTML = btn.dataset.orig || btn.innerHTML;
    btn.disabled = false;
  }
}

function fmt(v) {
  if (v == null) return '-';
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function fmtDate(d) {
  if (!d) return '-';
  return new Date(d).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

function estoqueClass(n) {
  if (n == null || n === 0) return 'estoque-zero';
  if (n < 5) return 'estoque-warn';
  return 'estoque-ok';
}

/* ─────────────── SIDEBAR MOBILE ─────────────── */
function initSidebar() {
  const btn = document.getElementById('btnMenu');
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('sidebarOverlay');
  if (!btn) return;
  btn.addEventListener('click', () => {
    sidebar.classList.toggle('open');
    overlay.classList.toggle('open');
  });
  overlay?.addEventListener('click', () => {
    sidebar.classList.remove('open');
    overlay.classList.remove('open');
  });
}

/* ─────────────── LOGOUT ─────────────── */
function logout() {
  clearAuth();
  location.href = '/login';
}

/* ─────────────── MODAL HELPERS ─────────────── */
function openModal(id) { document.getElementById(id)?.classList.add('open'); }
function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }

/* ─────────────── EXCEL EXPORT (SheetJS CDN) ─────────────── */
function exportExcel(data, filename, cols) {
  if (!window.XLSX) { toast('Biblioteca Excel não carregada', 'err'); return; }
  const rows = data.map(r => {
    const obj = {};
    cols.forEach(c => { obj[c.label] = r[c.key] ?? ''; });
    return obj;
  });
  const ws = XLSX.utils.json_to_sheet(rows);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, 'Dados');
  XLSX.writeFile(wb, filename + '_' + new Date().toISOString().slice(0,10) + '.xlsx');
  toast('Planilha exportada com sucesso! 📊');
}

/* ─────────────── LOGIN PAGE ─────────────── */
async function initLogin() {
  if (getToken()) { location.href = '/dashboard'; return; }

  document.getElementById('formLogin')?.addEventListener('submit', async e => {
    e.preventDefault();
    const btn = e.target.querySelector('[type=submit]');
    setLoading(btn, true);
    try {
      const data = await api('POST', '/auth/login', {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
      });
      setToken(data.token);
      location.href = '/dashboard';
    } catch (err) {
      toast(err.message || 'Email ou senha inválidos', 'err');
    } finally { setLoading(btn, false); }
  });
}

/* ─────────────── DASHBOARD ─────────────── */
async function initDashboard() {
  guardAuth();
  initSidebar();

  const [produtos, movimentacoes] = await Promise.all([
    api('GET', '/api/produto'),
    api('GET', '/api/movimentacoes')
  ]);

  // Stats
  const totalProd = produtos?.length ?? 0;
  const totalEstoque = produtos?.reduce((s, p) => s + (p.estoqueAtual || 0), 0) ?? 0;
  const entradas = movimentacoes?.filter(m => m.tipo === 'ENTRADA').length ?? 0;
  const saidas   = movimentacoes?.filter(m => m.tipo === 'SAIDA').length ?? 0;

  document.getElementById('statProdutos').textContent = totalProd;
  document.getElementById('statEstoque').textContent  = totalEstoque;
  document.getElementById('statEntradas').textContent = entradas;
  document.getElementById('statSaidas').textContent   = saidas;

  // Tabela últimas movimentações
  const tbody = document.getElementById('tbodyMovRecentes');
  if (tbody && movimentacoes) {
    const recentes = [...movimentacoes].reverse().slice(0, 10);
    tbody.innerHTML = recentes.length ? recentes.map(m => `
      <tr>
        <td>${m.produtoNome || '-'}</td>
        <td><span class="badge badge-${m.tipo === 'ENTRADA' ? 'entrada' : 'saida'}">${m.tipo}</span></td>
        <td>${m.quantidade}</td>
        <td>${fmtDate(m.data)}</td>
        <td class="${estoqueClass(m.estoqueAtual)}">${m.estoqueAtual ?? '-'}</td>
      </tr>`).join('') : '<tr><td colspan="5" class="table-empty">Nenhuma movimentação</td></tr>';
  }

  // Gráfico movimentações (últimos 7 dias)
  renderMovChart(movimentacoes || []);
  // Gráfico produtos por estoque
  renderEstoqueChart(produtos || []);
}

function renderMovChart(movs) {
  const ctx = document.getElementById('chartMovs');
  if (!ctx || !window.Chart) return;
  const dias = 7;
  const labels = [], entradas = [], saidas = [];
  for (let i = dias - 1; i >= 0; i--) {
    const d = new Date(); d.setDate(d.getDate() - i);
    const label = d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
    const dateStr = d.toISOString().slice(0, 10);
    labels.push(label);
    entradas.push(movs.filter(m => m.tipo === 'ENTRADA' && m.data?.startsWith(dateStr)).reduce((s, m) => s + m.quantidade, 0));
    saidas.push(movs.filter(m => m.tipo === 'SAIDA' && m.data?.startsWith(dateStr)).reduce((s, m) => s + m.quantidade, 0));
  }
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [
        { label: 'Entradas', data: entradas, backgroundColor: '#16a34a', borderRadius: 6 },
        { label: 'Saídas',   data: saidas,   backgroundColor: '#dc2626', borderRadius: 6 }
      ]
    },
    options: { responsive: true, plugins: { legend: { position: 'top' } }, scales: { y: { beginAtZero: true } } }
  });
}

function renderEstoqueChart(prods) {
  const ctx = document.getElementById('chartEstoque');
  if (!ctx || !window.Chart) return;
  const top = [...prods].sort((a, b) => (b.estoqueAtual || 0) - (a.estoqueAtual || 0)).slice(0, 8);
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: top.map(p => p.name),
      datasets: [{ data: top.map(p => p.estoqueAtual || 0), backgroundColor: ['#2563eb','#f59e0b','#16a34a','#dc2626','#8b5cf6','#06b6d4','#ec4899','#84cc16'] }]
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
  });
}

/* ─────────────── PRODUTOS PAGE ─────────────── */
let _produtos = [], _fornecedores = [], _editProdId = null;

async function initProdutos() {
  guardAuth();
  initSidebar();
  await loadProdutos();
  await loadFornecedoresSelect();

  document.getElementById('btnNovoProduto')?.addEventListener('click', () => {
    _editProdId = null;
    document.getElementById('modalProdTitle').textContent = 'Novo Produto';
    document.getElementById('formProduto').reset();
    document.getElementById('inpProdId').value = '';
    openModal('modalProduto');
  });

  document.getElementById('formProduto')?.addEventListener('submit', saveProduto);
  document.getElementById('searchProduto')?.addEventListener('input', filterProdutos);

  document.getElementById('btnExportProdutos')?.addEventListener('click', () => {
    exportExcel(_produtos, 'produtos', [
      { key: 'id', label: 'ID' },
      { key: 'name', label: 'Nome' },
      { key: 'descricao', label: 'Descrição' },
      { key: 'preco', label: 'Preço' },
      { key: 'fornecedorNome', label: 'Fornecedor' },
      { key: 'estoqueAtual', label: 'Estoque' }
    ]);
  });
}

async function loadProdutos() {
  const tbody = document.getElementById('tbodyProdutos');
  tbody.innerHTML = '<tr><td colspan="6" class="table-empty"><div class="spinner" style="border-color:#cbd5e1;border-top-color:#2563eb;width:24px;height:24px;margin:auto"></div></td></tr>';
  _produtos = await api('GET', '/api/produto') || [];
  renderTabelaProdutos(_produtos);
}

function renderTabelaProdutos(list) {
  const tbody = document.getElementById('tbodyProdutos');
  if (!list.length) {
    tbody.innerHTML = '<tr><td colspan="6" class="table-empty"><div class="icon">📦</div><div>Nenhum produto cadastrado</div></td></tr>';
    return;
  }
  tbody.innerHTML = list.map(p => `
    <tr>
      <td><strong>${p.name}</strong></td>
      <td>${p.descricao || '-'}</td>
      <td>${fmt(p.preco)}</td>
      <td>${p.fornecedorNome || '-'}</td>
      <td class="${estoqueClass(p.estoqueAtual)}">${p.estoqueAtual ?? 0}</td>
      <td>
        <button class="btn btn-icon btn-edit" onclick="editProduto(${p.id})" title="Editar">✏️</button>
        <button class="btn btn-icon btn-del"  onclick="deleteProduto(${p.id})" title="Excluir">🗑️</button>
      </td>
    </tr>`).join('');
}

function filterProdutos() {
  const q = document.getElementById('searchProduto').value.toLowerCase();
  renderTabelaProdutos(_produtos.filter(p => p.name?.toLowerCase().includes(q) || p.fornecedorNome?.toLowerCase().includes(q)));
}

async function loadFornecedoresSelect() {
  _fornecedores = await api('GET', '/api/fornecedores') || [];
  const sel = document.getElementById('selFornecedorProd');
  if (!sel) return;
  sel.innerHTML = '<option value="">— Sem fornecedor —</option>' +
    _fornecedores.map(f => `<option value="${f.id}">${f.nome}</option>`).join('');
}

async function editProduto(id) {
  const p = _produtos.find(x => x.id === id);
  if (!p) return;
  _editProdId = id;
  document.getElementById('modalProdTitle').textContent = 'Editar Produto';
  document.getElementById('inpProdId').value = id;
  document.getElementById('inpProdNome').value = p.name;
  document.getElementById('inpProdDesc').value = p.descricao || '';
  document.getElementById('inpProdPreco').value = p.preco || '';
  // Localiza fornecedor
  const sel = document.getElementById('selFornecedorProd');
  if (sel) {
    const forn = _fornecedores.find(f => f.nome === p.fornecedorNome);
    sel.value = forn ? forn.id : '';
  }
  openModal('modalProduto');
}

async function saveProduto(e) {
  e.preventDefault();
  const btn = e.target.querySelector('[type=submit]');
  setLoading(btn, true);
  const body = {
    name:        document.getElementById('inpProdNome').value,
    descricao:   document.getElementById('inpProdDesc').value,
    preco:       document.getElementById('inpProdPreco').value || null,
    fornecedorId: document.getElementById('selFornecedorProd').value || null
  };
  try {
    if (_editProdId) {
      await api('PUT', `/api/produto/${_editProdId}`, body);
      toast('Produto atualizado! ✅');
    } else {
      await api('POST', '/api/produto', body);
      toast('Produto criado! ✅');
    }
    closeModal('modalProduto');
    await loadProdutos();
  } catch (err) { toast(err.message, 'err'); }
  finally { setLoading(btn, false); }
}

async function deleteProduto(id) {
  if (!confirm('Excluir este produto?')) return;
  try {
    await api('DELETE', `/api/produto/${id}`);
    toast('Produto excluído');
    await loadProdutos();
  } catch (err) { toast(err.message, 'err'); }
}

/* ─────────────── FORNECEDORES PAGE ─────────────── */
let _fornecsList = [], _editFornId = null;

async function initFornecedores() {
  guardAuth();
  initSidebar();
  await loadFornecedores();

  document.getElementById('btnNovoFornecedor')?.addEventListener('click', () => {
    _editFornId = null;
    document.getElementById('modalFornTitle').textContent = 'Novo Fornecedor';
    document.getElementById('formFornecedor').reset();
    openModal('modalFornecedor');
  });

  document.getElementById('formFornecedor')?.addEventListener('submit', saveFornecedor);
  document.getElementById('searchFornecedor')?.addEventListener('input', filterFornecedores);

  document.getElementById('btnExportFornecedores')?.addEventListener('click', () => {
    exportExcel(_fornecsList, 'fornecedores', [
      { key: 'id', label: 'ID' },
      { key: 'nome', label: 'Nome' },
      { key: 'telefone', label: 'Telefone' },
      { key: 'email', label: 'E-mail' },
      { key: 'endereco', label: 'Endereço' }
    ]);
  });
}

async function loadFornecedores() {
  const tbody = document.getElementById('tbodyFornecedores');
  tbody.innerHTML = '<tr><td colspan="5" class="table-empty"><div class="spinner" style="border-color:#cbd5e1;border-top-color:#2563eb;width:24px;height:24px;margin:auto"></div></td></tr>';
  _fornecsList = await api('GET', '/api/fornecedores') || [];
  renderTabelaFornecedores(_fornecsList);
}

function renderTabelaFornecedores(list) {
  const tbody = document.getElementById('tbodyFornecedores');
  if (!list.length) {
    tbody.innerHTML = '<tr><td colspan="5" class="table-empty"><div class="icon">🏢</div><div>Nenhum fornecedor cadastrado</div></td></tr>';
    return;
  }
  tbody.innerHTML = list.map(f => `
    <tr>
      <td><strong>${f.nome}</strong></td>
      <td>${f.telefone || '-'}</td>
      <td>${f.email || '-'}</td>
      <td>${f.endereco || '-'}</td>
      <td>
        <button class="btn btn-icon btn-edit" onclick="editFornecedor(${f.id})" title="Editar">✏️</button>
        <button class="btn btn-icon btn-del"  onclick="deleteFornecedor(${f.id})" title="Excluir">🗑️</button>
      </td>
    </tr>`).join('');
}

function filterFornecedores() {
  const q = document.getElementById('searchFornecedor').value.toLowerCase();
  renderTabelaFornecedores(_fornecsList.filter(f => f.nome?.toLowerCase().includes(q) || f.email?.toLowerCase().includes(q)));
}

async function editFornecedor(id) {
  const f = _fornecsList.find(x => x.id === id);
  if (!f) return;
  _editFornId = id;
  document.getElementById('modalFornTitle').textContent = 'Editar Fornecedor';
  document.getElementById('inpFornNome').value     = f.nome;
  document.getElementById('inpFornTel').value      = f.telefone || '';
  document.getElementById('inpFornEmail').value    = f.email || '';
  document.getElementById('inpFornEnder').value    = f.endereco || '';
  openModal('modalFornecedor');
}

async function saveFornecedor(e) {
  e.preventDefault();
  const btn = e.target.querySelector('[type=submit]');
  setLoading(btn, true);
  const body = {
    nome:     document.getElementById('inpFornNome').value,
    telefone: document.getElementById('inpFornTel').value,
    email:    document.getElementById('inpFornEmail').value,
    endereco: document.getElementById('inpFornEnder').value
  };
  try {
    if (_editFornId) {
      await api('PUT', `/api/fornecedores/${_editFornId}`, body);
      toast('Fornecedor atualizado! ✅');
    } else {
      await api('POST', '/api/fornecedores', body);
      toast('Fornecedor criado! ✅');
    }
    closeModal('modalFornecedor');
    await loadFornecedores();
  } catch (err) { toast(err.message, 'err'); }
  finally { setLoading(btn, false); }
}

async function deleteFornecedor(id) {
  if (!confirm('Excluir este fornecedor?')) return;
  try {
    await api('DELETE', `/api/fornecedores/${id}`);
    toast('Fornecedor excluído');
    await loadFornecedores();
  } catch (err) { toast(err.message, 'err'); }
}

/* ─────────────── MOVIMENTAÇÕES PAGE ─────────────── */
let _movs = [], _movFiltro = 'TODOS';

async function initMovimentacoes() {
  guardAuth();
  initSidebar();
  await loadMovimentacoes();
  await loadProdutosSelectMov();

  document.getElementById('btnNovaMovimentacao')?.addEventListener('click', () => {
    document.getElementById('formMovimentacao').reset();
    openModal('modalMovimentacao');
  });

  document.getElementById('formMovimentacao')?.addEventListener('submit', saveMovimentacao);
  document.getElementById('searchMov')?.addEventListener('input', filterMovs);

  document.querySelectorAll('.filter-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      _movFiltro = tab.dataset.tipo;
      filterMovs();
    });
  });

  document.getElementById('btnExportMovs')?.addEventListener('click', () => {
    const lista = getMovsFiltrados();
    exportExcel(lista, 'movimentacoes', [
      { key: 'id', label: 'ID' },
      { key: 'produtoNome', label: 'Produto' },
      { key: 'tipo', label: 'Tipo' },
      { key: 'quantidade', label: 'Quantidade' },
      { key: 'data', label: 'Data' },
      { key: 'estoqueAtual', label: 'Estoque Atual' },
      { key: 'observacao', label: 'Observação' }
    ]);
  });
}

async function loadMovimentacoes() {
  const tbody = document.getElementById('tbodyMovs');
  tbody.innerHTML = '<tr><td colspan="6" class="table-empty"><div class="spinner" style="border-color:#cbd5e1;border-top-color:#2563eb;width:24px;height:24px;margin:auto"></div></td></tr>';
  _movs = await api('GET', '/api/movimentacoes') || [];
  renderTabelaMovs(_movs);
}

function getMovsFiltrados() {
  const q = document.getElementById('searchMov')?.value.toLowerCase() || '';
  return _movs.filter(m => {
    const tipoOk = _movFiltro === 'TODOS' || m.tipo === _movFiltro;
    const searchOk = !q || m.produtoNome?.toLowerCase().includes(q);
    return tipoOk && searchOk;
  });
}

function filterMovs() { renderTabelaMovs(getMovsFiltrados()); }

function renderTabelaMovs(list) {
  const tbody = document.getElementById('tbodyMovs');
  if (!list.length) {
    tbody.innerHTML = '<tr><td colspan="6" class="table-empty"><div class="icon">📋</div><div>Nenhuma movimentação encontrada</div></td></tr>';
    return;
  }
  tbody.innerHTML = [...list].reverse().map(m => `
    <tr>
      <td><strong>${m.produtoNome || '-'}</strong></td>
      <td><span class="badge badge-${m.tipo === 'ENTRADA' ? 'entrada' : 'saida'}">${m.tipo === 'ENTRADA' ? '⬆ ENTRADA' : '⬇ SAÍDA'}</span></td>
      <td><strong>${m.quantidade}</strong></td>
      <td>${fmtDate(m.data)}</td>
      <td class="${estoqueClass(m.estoqueAtual)}">${m.estoqueAtual ?? '-'}</td>
      <td>${m.observacao || '-'}</td>
    </tr>`).join('');
}

async function loadProdutosSelectMov() {
  const prods = await api('GET', '/api/produto') || [];
  const sel = document.getElementById('selProdutoMov');
  if (!sel) return;
  sel.innerHTML = '<option value="">Selecione o produto</option>' +
    prods.map(p => `<option value="${p.id}">${p.name} (estoque: ${p.estoqueAtual ?? 0})</option>`).join('');
}

async function saveMovimentacao(e) {
  e.preventDefault();
  const btn = e.target.querySelector('[type=submit]');
  setLoading(btn, true);
  const body = {
    produtoId:  parseInt(document.getElementById('selProdutoMov').value),
    quantidade: parseInt(document.getElementById('inpMovQtd').value),
    tipo:       document.getElementById('selMovTipo').value,
    observacao: document.getElementById('inpMovObs').value
  };
  try {
    await api('POST', '/api/movimentacoes', body);
    toast('Movimentação registrada! ✅');
    closeModal('modalMovimentacao');
    await loadMovimentacoes();
    await loadProdutosSelectMov();
  } catch (err) { toast(err.message, 'err'); }
  finally { setLoading(btn, false); }
}
