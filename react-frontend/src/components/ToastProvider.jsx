import React, { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext();

export function useToast() {
  return useContext(ToastContext);
}

export const ToastProvider = ({ children }) => {
  const [toast, setToast] = useState({ message: '', type: '', visible: false });

  const showToast = useCallback((message, type = 'info') => {
    setToast({ message, type, visible: true });
    setTimeout(() => {
      setToast((prev) => ({ ...prev, visible: false }));
    }, 3000);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className={`toast ${toast.type === 'error' ? 'toast-error' : 'toast-success'} ${toast.visible ? 'show' : ''}`}>
        {toast.message}
      </div>
    </ToastContext.Provider>
  );
};
