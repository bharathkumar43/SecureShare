const AuditTrail = ({ logs }) => (
  <div className="card mt-4">
    <div className="card-header">Audit Trail</div>
    <ul className="list-group list-group-flush">
      {logs.map((log, idx) => (
        <li className="list-group-item" key={idx}>{log.action} - {log.status}</li>
      ))}
    </ul>
  </div>
);
export default AuditTrail;