import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';

const ShareForm = ({ formData, setFormData, generateLink, cloudTokens }) => {
  const onDrop = useCallback((acceptedFiles) => {
    setFormData(prev => ({
      ...prev,
      selectedFiles: [...prev.selectedFiles, ...acceptedFiles.map(file => ({
        name: file.name,
        size: file.size,
        type: file.type,
        file: file
      }))]
    }));
  }, [setFormData]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: true
  });

  const removeFile = (index) => {
    setFormData(prev => ({
      ...prev,
      selectedFiles: prev.selectedFiles.filter((_, i) => i !== index)
    }));
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getMinDateTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes() + 30); // Minimum 30 minutes from now
    return now.toISOString().slice(0, 16);
  };

  return (
    <div className="card p-4 shadow-sm fade-in">
      <h4 className="card-title mb-4 text-center">
        <i className="fas fa-share-alt me-2"></i>
        Share Files Securely
      </h4>
      
      {/* File Upload Section */}
      <div className="mb-4">
        <label className="form-label fw-bold">Select Files to Share</label>
        <div
          {...getRootProps()}
          className={`file-upload-zone ${isDragActive ? 'active' : ''}`}
        >
          <input {...getInputProps()} />
          <i className="fas fa-cloud-upload-alt fa-3x mb-3 text-primary"></i>
          {isDragActive ? (
            <p className="mb-0">Drop the files here...</p>
          ) : (
            <div>
              <p className="mb-2">Drag & drop files here, or click to select</p>
              <small className="text-muted">Support for multiple files</small>
            </div>
          )}
        </div>
        
        {/* Selected Files Display */}
        {formData.selectedFiles.length > 0 && (
          <div className="mt-3">
            <h6>Selected Files ({formData.selectedFiles.length})</h6>
            {formData.selectedFiles.map((file, index) => (
              <div key={index} className="file-item slide-in">
                <div className="d-flex align-items-center">
                  <div className="file-icon">
                    {file.name.split('.').pop().toUpperCase().slice(0, 3)}
                  </div>
                  <div>
                    <div className="fw-bold">{file.name}</div>
                    <small className="text-muted">{formatFileSize(file.size)}</small>
                  </div>
                </div>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-danger"
                  onClick={() => removeFile(index)}
                >
                  <i className="fas fa-times"></i>
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="row">
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">Source Cloud</label>
          <select 
            className="form-select" 
            value={formData.sourceCloud}
            onChange={e => setFormData({ ...formData, sourceCloud: e.target.value })}
          >
            <option value="">Select Source Cloud</option>
            <option value="Google Drive" disabled={!cloudTokens.googledrive}>
              Google Drive {!cloudTokens.googledrive && '(Not Connected)'}
            </option>
            <option value="Dropbox" disabled={!cloudTokens.dropbox}>
              Dropbox {!cloudTokens.dropbox && '(Not Connected)'}
            </option>
          </select>
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">Destination Cloud</label>
          <select 
            className="form-select" 
            value={formData.destinationCloud}
            onChange={e => setFormData({ ...formData, destinationCloud: e.target.value })}
          >
            <option value="">Select Destination Cloud</option>
            <option value="Google Drive" disabled={!cloudTokens.googledrive}>
              Google Drive {!cloudTokens.googledrive && '(Not Connected)'}
            </option>
            <option value="Dropbox" disabled={!cloudTokens.dropbox}>
              Dropbox {!cloudTokens.dropbox && '(Not Connected)'}
            </option>
          </select>
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">From Email</label>
          <input 
            type="email" 
            className="form-control" 
            value={formData.fromEmail}
            onChange={e => setFormData({ ...formData, fromEmail: e.target.value })}
            placeholder="sender@example.com"
          />
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">To Email</label>
          <input 
            type="email" 
            className="form-control" 
            value={formData.toEmail}
            onChange={e => setFormData({ ...formData, toEmail: e.target.value })}
            placeholder="recipient@example.com"
          />
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">Password Protection</label>
          <input 
            type="password" 
            className="form-control" 
            value={formData.password}
            onChange={e => setFormData({ ...formData, password: e.target.value })}
            placeholder="Enter secure password"
          />
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">Expiry Date & Time</label>
          <input 
            type="datetime-local" 
            className="form-control" 
            value={formData.expiry}
            min={getMinDateTime()}
            onChange={e => setFormData({ ...formData, expiry: e.target.value })}
          />
        </div>
        
        <div className="col-md-6 mb-3">
          <label className="form-label fw-bold">Download Limit</label>
          <select 
            className="form-select" 
            value={formData.downloadLimit}
            onChange={e => setFormData({ ...formData, downloadLimit: e.target.value })}
          >
            <option value="">Select Download Limit</option>
            <option value="1">1 Download</option>
            <option value="3">3 Downloads</option>
            <option value="5">5 Downloads</option>
            <option value="10">10 Downloads</option>
            <option value="unlimited">Unlimited</option>
          </select>
        </div>
        
        <div className="col-md-6 mb-3 d-flex align-items-center">
          <div className="form-check">
            <input 
              type="checkbox" 
              className="form-check-input" 
              checked={formData.watermark}
              onChange={e => setFormData({ ...formData, watermark: e.target.checked })}
            />
            <label className="form-check-label fw-bold">
              Add Watermark
            </label>
          </div>
        </div>
        
        <div className="col-12 text-center">
          <button 
            className="btn btn-primary btn-lg px-5" 
            onClick={generateLink}
            disabled={formData.selectedFiles.length === 0}
          >
            <i className="fas fa-link me-2"></i>
            Generate Secure Link
          </button>
        </div>
      </div>
    </div>
  );
};

export default ShareForm;