import React from 'react';

const ShareForm = ({ formData, setFormData, generateLink }) => {
  return (
    <div className="card p-4 shadow-sm mt-4">
      <h4 className="card-title mb-3 text-center">Share File</h4>
      <div className="row">
        <div className="col-md-6 mb-3">
          <label className="form-label">Source Cloud</label>
          <select className="form-control" value={formData.sourceCloud}
  onChange={e => setFormData({ ...formData, sourceCloud: e.target.value })}>
  <option value="">Select Source Cloud</option>
  <option value="Dropbox">Dropbox</option>
  <option value="Google Drive">Google Drive</option>
  <option value="OneDrive">OneDrive</option>
</select>

        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">Destination Cloud</label>
          <select className="form-control" value={formData.destinationCloud}
  onChange={e => setFormData({ ...formData, destinationCloud: e.target.value })}>
  <option value="">Select Destination Cloud</option>
  <option value="Dropbox">Dropbox</option>
  <option value="Google Drive">Google Drive</option>
  <option value="OneDrive">OneDrive</option>
</select>

        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">From Email</label>
          <input type="email" className="form-control" value={formData.fromEmail}
            onChange={e => setFormData({ ...formData, fromEmail: e.target.value })} />
        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">To Email</label>
          <input type="email" className="form-control" value={formData.toEmail}
            onChange={e => setFormData({ ...formData, toEmail: e.target.value })} />
        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">Password</label>
          <input type="password" className="form-control" value={formData.password}
            onChange={e => setFormData({ ...formData, password: e.target.value })} />
        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">Expiry Date & Time</label>
          <input type="datetime-local" className="form-control" value={formData.expiry}
            onChange={e => setFormData({ ...formData, expiry: e.target.value })} />
        </div>
        <div className="col-md-6 mb-3">
          <label className="form-label">Download Limit</label>
          <input type="number" className="form-control" value={formData.downloadLimit}
            onChange={e => setFormData({ ...formData, downloadLimit: e.target.value })} />
        </div>
        <div className="col-md-6 mb-3 d-flex align-items-center">
          <input type="checkbox" className="form-check-input me-2" checked={formData.watermark}
            onChange={e => setFormData({ ...formData, watermark: e.target.checked })} />
          <label className="form-check-label">Watermark</label>
        </div>
        <div className="text-center">
          <button className="btn btn-primary px-4" onClick={generateLink}>Generate Secure Link</button>
        </div>
      </div>
    </div>
  );
};

export default ShareForm;
