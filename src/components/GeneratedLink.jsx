import React, { useState } from 'react';

const GeneratedLink = ({ link, revokeLink }) => {
  const [copied, setCopied] = useState(false);

  const copyToClipboard = () => {
    navigator.clipboard.writeText(link);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  if (!link) return null;

  return (
    <div className="alert alert-success mt-4 fade-in">
      <div className="d-flex justify-content-between align-items-start">
        <div className="flex-grow-1">
          <h5 className="alert-heading">
            <i className="fas fa-check-circle me-2"></i>
            Secure Link Generated!
          </h5>
          <p className="mb-2">Your secure sharing link has been created and sent via email.</p>
          <div className="input-group mb-3">
            <input 
              type="text" 
              className="form-control" 
              value={link} 
              readOnly 
            />
            <button 
              className="btn btn-outline-primary" 
              onClick={copyToClipboard}
            >
              <i className={`fas ${copied ? 'fa-check' : 'fa-copy'} me-1`}></i>
              {copied ? 'Copied!' : 'Copy'}
            </button>
          </div>
          <small className="text-muted">
            <i className="fas fa-info-circle me-1"></i>
            The recipient will receive an email with download instructions and password.
          </small>
        </div>
        <button 
          className="btn btn-danger btn-sm ms-3" 
          onClick={revokeLink}
        >
          <i className="fas fa-ban me-1"></i>
          Revoke Link
        </button>
      </div>
    </div>
  );
};

export default GeneratedLink;