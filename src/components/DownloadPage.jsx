import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';

const DownloadPage = () => {
  const { shareId } = useParams();
  const [shareData, setShareData] = useState(null);
  const [password, setPassword] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);

  useEffect(() => {
    fetchShareData();
  }, [shareId]);

  const fetchShareData = async () => {
    try {
      const response = await fetch(`http://localhost:8085/api/share/${shareId}`);
      if (response.ok) {
        const data = await response.json();
        setShareData(data);
      } else {
        toast.error('Invalid or expired link');
      }
    } catch (error) {
      toast.error('Error loading share data');
    } finally {
      setLoading(false);
    }
  };

  const verifyPassword = async () => {
    try {
      const response = await fetch(`http://localhost:8085/api/verify-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ shareId, password })
      });

      if (response.ok) {
        setIsAuthenticated(true);
        toast.success('Password verified successfully!');
      } else {
        toast.error('Incorrect password');
      }
    } catch (error) {
      toast.error('Error verifying password');
    }
  };

  const downloadFiles = async () => {
    setDownloading(true);
    try {
      const response = await fetch(`http://localhost:8085/api/download/${shareId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password })
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `shared-files-${shareId}.zip`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        toast.success('Download started successfully!');
      } else {
        const error = await response.json();
        toast.error(error.message || 'Download failed');
      }
    } catch (error) {
      toast.error('Error downloading files');
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return (
      <div className="download-page">
        <div className="card download-card">
          <div className="card-body text-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-3">Loading share data...</p>
          </div>
        </div>
      </div>
    );
  }

  if (!shareData) {
    return (
      <div className="download-page">
        <div className="card download-card">
          <div className="card-body text-center">
            <i className="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
            <h4>Link Not Found</h4>
            <p>This sharing link is invalid or has expired.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="download-page">
      <div className="card download-card fade-in">
        <div className="card-header text-center">
          <h4 className="mb-0">
            <i className="fas fa-download me-2"></i>
            Secure File Download
          </h4>
        </div>
        <div className="card-body">
          {!isAuthenticated ? (
            <div>
              <div className="text-center mb-4">
                <i className="fas fa-lock fa-3x text-primary mb-3"></i>
                <h5>Password Required</h5>
                <p className="text-muted">
                  This content is password protected. Please enter the password to continue.
                </p>
              </div>
              
              <div className="mb-3">
                <label className="form-label">Password</label>
                <input
                  type="password"
                  className="form-control"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && verifyPassword()}
                  placeholder="Enter password"
                />
              </div>
              
              <button
                className="btn btn-primary w-100"
                onClick={verifyPassword}
                disabled={!password}
              >
                <i className="fas fa-unlock me-2"></i>
                Verify Password
              </button>
            </div>
          ) : (
            <div>
              <div className="text-center mb-4">
                <i className="fas fa-check-circle fa-3x text-success mb-3"></i>
                <h5>Access Granted</h5>
                <p className="text-muted">You can now download the shared files.</p>
              </div>
              
              <div className="mb-4">
                <h6>Share Details:</h6>
                <ul className="list-unstyled">
                  <li><strong>From:</strong> {shareData.fromEmail}</li>
                  <li><strong>Files:</strong> {shareData.fileCount} file(s)</li>
                  <li><strong>Expires:</strong> {new Date(shareData.expiry).toLocaleString()}</li>
                  <li><strong>Downloads Remaining:</strong> {shareData.remainingDownloads}</li>
                </ul>
              </div>
              
              <button
                className="btn btn-success w-100"
                onClick={downloadFiles}
                disabled={downloading || shareData.remainingDownloads <= 0}
              >
                {downloading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Preparing Download...
                  </>
                ) : (
                  <>
                    <i className="fas fa-download me-2"></i>
                    Download Files
                  </>
                )}
              </button>
              
              {shareData.remainingDownloads <= 0 && (
                <div className="alert alert-warning mt-3">
                  <i className="fas fa-exclamation-triangle me-2"></i>
                  Download limit reached. This link is no longer active.
                </div>
              )}
            </div>
          )}
        </div>
        
        <div className="card-footer text-center">
          <small className="text-muted">
            <i className="fas fa-shield-alt me-1"></i>
            Secured by CloudFuze SecureShare
          </small>
        </div>
      </div>
    </div>
  );
};

export default DownloadPage;