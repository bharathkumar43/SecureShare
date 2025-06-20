import React from 'react';

const CloudAuth = ({ cloudTokens, setCloudTokens }) => {
  const authenticateCloud = async (cloudProvider) => {
    try {
      const response = await fetch(`http://localhost:8085/api/auth/${cloudProvider}`, {
        method: 'GET',
      });
      
      if (response.ok) {
        const data = await response.json();
        // In a real implementation, this would redirect to OAuth flow
        // For now, we'll simulate successful authentication
        setCloudTokens(prev => ({
          ...prev,
          [cloudProvider]: 'authenticated_token_' + Date.now()
        }));
        
        // Open OAuth window (in real implementation)
        window.open(data.authUrl, 'oauth', 'width=500,height=600');
      }
    } catch (error) {
      console.error('Authentication error:', error);
    }
  };

  return (
    <div className="card cloud-auth-card mb-4 fade-in">
      <div className="card-header">
        <h5 className="mb-0">
          <i className="fas fa-key me-2"></i>
          Cloud Authentication
        </h5>
      </div>
      <div className="card-body">
        <p className="text-muted mb-3">
          Connect your cloud storage accounts to enable file sharing
        </p>
        <div className="row">
          <div className="col-md-6 mb-2">
            <button
              className={`btn auth-button google w-100 ${cloudTokens.googledrive ? 'btn-success' : ''}`}
              onClick={() => authenticateCloud('googledrive')}
              disabled={cloudTokens.googledrive}
            >
              <i className="fab fa-google me-2"></i>
              {cloudTokens.googledrive ? 'Google Drive Connected' : 'Connect Google Drive'}
            </button>
          </div>
          <div className="col-md-6 mb-2">
            <button
              className={`btn auth-button dropbox w-100 ${cloudTokens.dropbox ? 'btn-success' : ''}`}
              onClick={() => authenticateCloud('dropbox')}
              disabled={cloudTokens.dropbox}
            >
              <i className="fab fa-dropbox me-2"></i>
              {cloudTokens.dropbox ? 'Dropbox Connected' : 'Connect Dropbox'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CloudAuth;