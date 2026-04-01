import React, { useState } from 'react';
import './TransactionModal.css';

export default function TransactionModal({ type, data, categories, sources, onSave, onClose }) {
  const [form, setForm] = useState({
    title:       data?.title       || '',
    amount:      data?.amount      || '',
    date:        data?.date        || new Date().toISOString().split('T')[0],
    description: data?.description || '',
    categoryId:  data?.categoryId  || (categories?.[0]?.id || ''),
    source:      data?.source      || (sources?.[0] || ''),
  });
  const [saving, setSaving] = useState(false);

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    await onSave({ ...form, amount: parseFloat(form.amount) });
    setSaving(false);
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal-box">
        <div className="modal-header">
          <h3>{data?.id ? 'Edit' : 'Add'} {type === 'expense' ? 'Expense' : 'Income'}</h3>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label>Title *</label>
            <input required value={form.title} onChange={e => set('title', e.target.value)} placeholder="e.g. Lunch at cafe" />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Amount *</label>
              <input required type="number" min="0.01" step="0.01" value={form.amount}
                onChange={e => set('amount', e.target.value)} placeholder="0.00" />
            </div>
            <div className="form-group">
              <label>Date *</label>
              <input required type="date" value={form.date} onChange={e => set('date', e.target.value)} />
            </div>
          </div>
          {type === 'expense' && categories && (
            <div className="form-group">
              <label>Category *</label>
              <select required value={form.categoryId} onChange={e => set('categoryId', parseInt(e.target.value))}>
                {categories.map(c => <option key={c.id} value={c.id}>{c.icon} {c.name}</option>)}
              </select>
            </div>
          )}
          {type === 'income' && sources && (
            <div className="form-group">
              <label>Source</label>
              <select value={form.source} onChange={e => set('source', e.target.value)}>
                {sources.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          )}
          <div className="form-group">
            <label>Description</label>
            <textarea rows={2} value={form.description} onChange={e => set('description', e.target.value)}
              placeholder="Optional note..." />
          </div>
          <div className="modal-actions">
            <button type="button" className="btn-cancel" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn-save" disabled={saving}>
              {saving ? 'Saving...' : (data?.id ? 'Update' : 'Add')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
