import React from 'react';

const AuditTrail = ({ logs }) => (
  <div className="card fade-in">
    <div className="card-header">
      <h5 className="mb-0">
        <i className="fas fa-history me-2"></i>
        Audit Trail
      </h5>
    </div>
    <div className="card-body">
      {logs.length === 0 ? (
        <p className="text-muted text-center mb-0">
          <i className="fas fa-clock me-2"></i>
          No activity yet
        </p>
      ) : (
        <div className="timeline">
          {logs.map((log, idx) => (
            <div key={idx} className="timeline-item slide-in">
              <div className="timeline-marker">
                <i className={`fas ${log.action === 'Generated' ? 'fa-plus-circle text-success' : 'fa-ban text-danger'}`}></i>
              </div>
              <div className="timeline-content">
                <h6 className="mb-1">{log.action}</h6>
                <p className="mb-1 text-muted small">{log.details}</p>
                <small className="text-muted">
                  <i className="fas fa-clock me-1"></i>
                  {log.timestamp}
                </small>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  </div>
);

export default AuditTrail;