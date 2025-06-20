import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import ShareForm from './components/ShareForm';
import GeneratedLink from './components/GeneratedLink';
import AuditTrail from './components/AuditTrail';
import Toast from './components/Toast';
import DownloadPage from './components/DownloadPage';
import CloudAuth from './components/CloudAuth';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

function App() {
  const [formData, setFormData] = useState({
    sourceCloud: '',
    destinationCloud: '',
    toEmail: '',
    fromEmail: '',
    watermark: false,
    password: '',
    expiry: '',
    downloadLimit: '',
    selectedFiles: []
  });

  const [link, setLink] = useState('');
  const [auditLogs, setAuditLogs] = useState([]);
  const [progress, setProgress] = useState(0);
  const [isGenerating, setIsGenerating] = useState(false);
  const [cloudTokens, setCloudTokens] = useState({
    googleDrive: null,
    dropbox: null
  });

  const validateForm = () => {
    const { sourceCloud, destinationCloud, toEmail, fromEmail, password, expiry, downloadLimit, selectedFiles } = formData;
    
    if (!sourceCloud || !destinationCloud || !toEmail || !fromEmail || !password || !expiry || !downloadLimit) {
      toast.error('Please fill in all required fields.');
      return false;
    }
    
    if (selectedFiles.length === 0) {
      toast.error('Please select at least one file to share.');
      return false;
    }
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(toEmail) || !emailRegex.test(fromEmail)) {
      toast.error('Please enter valid email addresses.');
      return false;
    }
    
    const expiryDate = new Date(expiry);
    if (expiryDate <= new Date()) {
      toast.error('Expiry date must be in the future.');
      return false;
    }
    
    return true;
  };

  const generateLink = async () => {
    if (!validateForm()) return;

    setIsGenerating(true);
    setProgress(0);

    // Simulate progress
    const progressInterval = setInterval(() => {
      setProgress((oldProgress) => {
        if (oldProgress >= 90) {
          clearInterval(progressInterval);
          return 90;
        }
        return oldProgress + 10;
      });
    }, 300);

    try {
      const payload = {
        ...formData,
        sourceCloudToken: cloudTokens[formData.sourceCloud.toLowerCase().replace(' ', '')],
        destinationCloudToken: cloudTokens[formData.destinationCloud.toLowerCase().replace(' ', '')]
      };

      const res = await fetch('http://localhost:8085/api/share', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await res.json();
      
      if (res.ok) {
        setProgress(100);
        setLink(data.link);
        setAuditLogs([...auditLogs, { 
          action: 'Generated', 
          status: 'Active', 
          timestamp: new Date().toLocaleString(),
          details: `Shared ${formData.selectedFiles.length} file(s) from ${formData.sourceCloud} to ${formData.destinationCloud}`
        }]);
        toast.success("Secure link generated and email sent!");
      } else {
        toast.error("Server error: " + (data.message || "Unknown error"));
      }
    } catch (err) {
      toast.error("Error generating link: " + err.message);
    } finally {
      clearInterval(progressInterval);
      setIsGenerating(false);
      setProgress(0);
    }
  };

  const revokeLink = async () => {
    try {
      await fetch('http://localhost:8085/api/revoke', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ link })
      });
      
      setLink('');
      setAuditLogs([...auditLogs, { 
        action: 'Revoked', 
        status: 'Revoked', 
        timestamp: new Date().toLocaleString(),
        details: 'Link access revoked by sender'
      }]);
      toast.info("Link revoked successfully.");
    } catch (err) {
      toast.error("Error revoking link: " + err.message);
    }
  };

  const MainApp = () => (
    <div>
      <Header />
      <div className="container mt-4">
        <div className="row">
          <div className="col-lg-8">
            <CloudAuth 
              cloudTokens={cloudTokens} 
              setCloudTokens={setCloudTokens} 
            />
            
            <ShareForm
              formData={formData}
              setFormData={setFormData}
              generateLink={generateLink}
              cloudTokens={cloudTokens}
            />

            {isGenerating && (
              <div className="card mt-3">
                <div className="card-body">
                  <h6 className="card-title">Processing your request...</h6>
                  <div className="progress">
                    <div
                      className="progress-bar progress-bar-striped progress-bar-animated bg-success"
                      role="progressbar"
                      style={{ width: `${progress}%` }}
                    >
                      {progress}%
                    </div>
                  </div>
                  <small className="text-muted mt-2 d-block">
                    {progress < 30 && "Authenticating with cloud services..."}
                    {progress >= 30 && progress < 60 && "Uploading files..."}
                    {progress >= 60 && progress < 90 && "Generating secure link..."}
                    {progress >= 90 && "Sending email notification..."}
                  </small>
                </div>
              </div>
            )}

            <GeneratedLink link={link} revokeLink={revokeLink} />
          </div>
          
          <div className="col-lg-4">
            <AuditTrail logs={auditLogs} />
          </div>
        </div>
      </div>
      <Toast />
    </div>
  );

  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainApp />} />
        <Route path="/download/:shareId" element={<DownloadPage />} />
      </Routes>
    </Router>
  );
}

export default App;