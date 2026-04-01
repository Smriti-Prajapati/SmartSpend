import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useCurrency } from '../context/CurrencyContext';
import './Profile.css';

const CURRENCIES = ['USD','EUR','GBP','INR','JPY','CAD','AUD'];

export default function Profile() {
  const { user } = useAuth();
  const { refresh: refreshCurrency } = useCurrency();
  const [profile, setProfile] = useState(null);
  const [form, setForm]       = useState({ name: '', currency: 'USD', monthlyBudget: '' });
  const [saving, setSaving]   = useState(false);
  const [dateRange, setDateRange] = useState({
    start: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
    end:   new Date().toISOString().split('T')[0],
  });

  useEffect(() => {
    api.get('/user/profile').then(r => {
      setProfile(r.data);
      setForm({ name: r.data.name, currency: r.data.currency, monthlyBudget: r.data.monthlyBudget || '' });
    });
  }, []);

  const handleSave = async (e) => {
    e.preventDefault(); setSaving(true);
    try {
      await api.put('/user/profile', form);
      refreshCurrency();
      toast.success('Profile updated');
    } catch { toast.error('Failed to update'); }
    finally { setSaving(false); }
  };

  const handleExport = async () => {
    try {
      const res = await api.get('/export/pdf', {
        params: { startDate: dateRange.start, endDate: dateRange.end },
        responseType: 'blob',
      });
      const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = 'smartspend-report.pdf'; a.click();
      URL.revokeObjectURL(url);
      toast.success('Report downloaded');
    } catch { toast.error('Export failed'); }
  };

  if (!profile) return <div className="page-loader"><div className="spinner" /></div>;

  return (
    <div className="profile-page fade-in">
      {/* Avatar card */}
      <div className="profile-hero">
        <div className="profile-avatar">{profile.name?.[0]?.toUpperCase()}</div>
        <div>
          <h2 className="profile-name">{profile.name}</h2>
          <p className="profile-email">{profile.email}</p>
          <p className="profile-since">Member since {profile.createdAt ? new Date(profile.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long' }) : 'N/A'}</p>
        </div>
      </div>

      <div className="profile-grid">
        {/* Edit form */}
        <div className="profile-card">
          <h3 className="card-title">Edit Profile</h3>
          <form onSubmit={handleSave} className="profile-form">
            <div className="form-group">
              <label>Full Name</label>
              <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
            </div>
            <div className="form-group">
              <label>Currency</label>
              <select value={form.currency} onChange={e => setForm(f => ({ ...f, currency: e.target.value }))}>
                {CURRENCIES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Monthly Budget ($)</label>
              <input type="number" min="0" step="0.01" value={form.monthlyBudget}
                onChange={e => setForm(f => ({ ...f, monthlyBudget: e.target.value }))}
                placeholder="Set a monthly spending limit" />
            </div>
            <button type="submit" className="btn-save-profile" disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </button>
          </form>
        </div>

        {/* Export */}
        <div className="profile-card">
          <h3 className="card-title">Export Report (PDF)</h3>
          <p className="card-desc">Download a detailed financial report for any date range.</p>
          <div className="form-group">
            <label>Start Date</label>
            <input type="date" value={dateRange.start} onChange={e => setDateRange(d => ({ ...d, start: e.target.value }))} />
          </div>
          <div className="form-group" style={{ marginTop: 12 }}>
            <label>End Date</label>
            <input type="date" value={dateRange.end} onChange={e => setDateRange(d => ({ ...d, end: e.target.value }))} />
          </div>
          <button className="btn-export" onClick={handleExport} style={{ marginTop: 20 }}>
            📄 Download PDF Report
          </button>
        </div>
      </div>
    </div>
  );
}
