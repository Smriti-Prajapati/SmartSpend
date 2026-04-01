import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const CurrencyContext = createContext(null);

const SYMBOLS = {
  USD: '$', EUR: '€', GBP: '£', INR: '₹',
  JPY: '¥', CAD: 'CA$', AUD: 'A$',
};

export function CurrencyProvider({ children }) {
  const [currency, setCurrency] = useState(
    () => localStorage.getItem('ss_currency') || 'USD'
  );

  useEffect(() => {
    // Load currency from profile on mount
    api.get('/user/profile')
      .then(r => {
        const cur = r.data.currency || 'USD';
        setCurrency(cur);
        localStorage.setItem('ss_currency', cur);
      })
      .catch(() => {});
  }, []);

  const symbol = SYMBOLS[currency] || currency;

  const fmt = (value) => {
    const num = (value || 0).toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
    return `${symbol}${num}`;
  };

  const refresh = () => {
    api.get('/user/profile').then(r => {
      const cur = r.data.currency || 'USD';
      setCurrency(cur);
      localStorage.setItem('ss_currency', cur);
    }).catch(() => {});
  };

  return (
    <CurrencyContext.Provider value={{ currency, symbol, fmt, refresh }}>
      {children}
    </CurrencyContext.Provider>
  );
}

export const useCurrency = () => useContext(CurrencyContext);
