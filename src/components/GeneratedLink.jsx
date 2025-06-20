const GeneratedLink = ({ link, revokeLink }) => link ? (
  <div className="alert alert-success mt-3">
    <strong>Generated Link:</strong> <a href={link} target="_blank" rel="noopener noreferrer">{link}</a>
    <button className="btn btn-danger btn-sm float-end" onClick={revokeLink}>Revoke Link</button>
  </div>
) : null;
export default GeneratedLink;