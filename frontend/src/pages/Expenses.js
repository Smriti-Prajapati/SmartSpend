import React, { useEffect, useState, useCallback } from 'react';
import toast from 'react-hot-toast';
import api from '../services/api';
import TransactionModal from '../components/TransactionModal';
import { useCurrency } from '../context/CurrencyContext';
import './Transactions.css';

export default function Expenses() {
  const [expenses, setExpenses]     = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading]       = useState(true);
  const [modal, setModal]           = useState(null); // null | 'add' | expense object
  const [search, setSearch]         = useState('');
  const [filterCat, setFilterCat]   = useState('');
  const [dateRange, setDateRange]   = useState({ start: '', end: '' });
  const { fmt } = useCurrency();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = {};
      if (search)              params.search = search;
      if (filterCat)           params.categoryId = filterCat;
      if (dateRange.start && dateRange.end) { params.startDate = dateRange.start; params.endDate = dateRange.end; }
      const [expRes, catRes] = await Promise.all([api.get('/expenses', { params }), api.get('/categories/expense')]);
      setExpenses(expRes.data);
      setCategories(catRes.data);
    } finally { setLoading(false); }
  }, [search, filterCat, dateRange]);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (form) => {
    try {
      if (modal?.id) { await api.put(`/expenses/${modal.id}`, form); toast.success('Expense updated'); }
      else           { await api.post('/expenses', form);            toast.success('Expense added'); }
      setModal(null); load();
    } catch (e) { toast.error(e.response?.data?.error || 'Error saving expense'); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this expense?')) return;
    try { await api.delete(`/expenses/${id}`); toast.success('Deleted'); load(); }
    catch { toast.error('Failed to delete'); }
  };

  const total = expenses.reduce((s, e) => s + e.amount, 0);

  return (
    <div className="tx-page fade-in">
      <div className="tx-header">
        <div>
          <h2 className="tx-heading">Expenses</h2>
          <p className="tx-sub">{expenses.length} records · Total: <strong style={{color:'#ef4444'}}>{fmt(total)}</strong></p>
        </div>
        <button className="btn-primary" onClick={() => setModal({})}>+ Add Expense</button>
      </div>

      {/* Filters */}
      <div className="filters-bar">
        <input className="filter-input" placeholder="🔍 Search..." value={search}
          onChange={e => setSearch(e.target.value)} />
        <select className="filter-select" value={filterCat} onChange={e => setFilterCat(e.target.value)}>
          <option value="">All Categories</option>
          {categories.map(c => <option key={c.id} value={c.id}>{c.icon} {c.name}</option>)}
        </select>
        <input type="date" className="filter-input" value={dateRange.start}
          onChange={e => setDateRange(d => ({ ...d, start: e.target.value }))} />
        <input type="date" className="filter-input" value={dateRange.end}
          onChange={e => setDateRange(d => ({ ...d, end: e.target.value }))} />
        <button className="btn-ghost" onClick={() => { setSearch(''); setFilterCat(''); setDateRange({ start: '', end: '' }); }}>
          Clear
        </button>
      </div>

      {/* Table */}
      {loading ? <div className="page-loader"><div className="spinner" /></div> : (
        <div className="tx-table-wrap">
          {expenses.length === 0
            ? <div className="empty-state">No expenses found. Add your first one!</div>
            : <table className="tx-table">
                <thead>
                  <tr><th>Title</th><th>Category</th><th>Amount</th><th>Date</th><th>Actions</th></tr>
                </thead>
                <tbody>
                  {expenses.map(e => (
                    <tr key={e.id}>
                      <td>
                        <div className="tx-title-cell">
                          <span className="cat-dot" style={{ background: e.categoryColor }}>{e.categoryIcon}</span>
                          <div>
                            <div className="cell-title">{e.title}</div>
                            {e.description && <div className="cell-sub">{e.description}</div>}
                          </div>
                        </div>
                      </td>
                      <td><span className="cat-badge" style={{ background: e.categoryColor + '22', color: e.categoryColor }}>{e.categoryName}</span></td>
                      <td><span className="amount-red">{fmt(e.amount)}</span></td>
                      <td className="date-cell">{e.date}</td>
                      <td>
                        <div className="action-btns">
                          <button className="btn-edit" onClick={() => setModal(e)}>✏️</button>
                          <button className="btn-del"  onClick={() => handleDelete(e.id)}>🗑️</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>}
        </div>
      )}

      {modal !== null && (
        <TransactionModal
          type="expense"
          data={modal}
          categories={categories}
          onSave={handleSave}
          onClose={() => setModal(null)}
        />
      )}
    </div>
  );
}
