import React from 'react';
import { useLocation } from 'react-router-dom';
import './Topbar.css';

const TITLES = {
  '/dashboard': 'Dashboard',
  '/expenses':  'Expenses',
  '/income':    'Income',
  '/insights':  'Insights',
  '/profile':   'Profile',
};

export default function Topbar({ onMenuClick }) {
  const { pathname } = useLocation();
  const title = TITLES[pathname] || 'SmartSpend';
  const today = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

  return (
    <header className="topbar">
      <div className="topbar-left">
        <button className="menu-btn" onClick={onMenuClick} aria-label="Toggle menu">☰</button>
        <div>
          <h1 className="topbar-title">{title}</h1>
          <p className="topbar-date">{today}</p>
        </div>
      </div>
    </header>
  );
}
