import { useState, useEffect, useContext, useRef } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';
import { UploadCloud, LogOut, Clock, FileAudio, FileText, Download, Trash2, Mic } from 'lucide-react';

const Dashboard = () => {
  const { user, logout } = useContext(AuthContext);
  const [history, setHistory] = useState([]);
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const fileInputRef = useRef(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const res = await api.get('/speech/history');
      setHistory(res.data);
    } catch (err) {
      console.error('Failed to fetch history', err);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);
    setLoading(true);

    try {
      const res = await api.post('/speech/upload', formData, {
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(percentCompleted);
        }
      });
      setHistory([res.data, ...history]);
      setFile(null);
      if (fileInputRef.current) fileInputRef.current.value = '';
    } catch (err) {
      console.error('Upload failed', err);
      alert('Upload failed. Please try again.');
    } finally {
      setLoading(false);
      setUploadProgress(0);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this transcription?')) return;
    try {
      await api.delete(`/speech/${id}`);
      setHistory(history.filter(item => item.id !== id));
    } catch (err) {
      console.error('Delete failed', err);
    }
  };

  const downloadText = (transcript, filename) => {
    const element = document.createElement("a");
    const file = new Blob([transcript], {type: 'text/plain'});
    element.href = URL.createObjectURL(file);
    element.download = `${filename}.txt`;
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-300">
      <nav className="bg-slate-900 border-b border-slate-800 px-6 py-4">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <div className="flex items-center gap-2">
            <div className="bg-indigo-500/20 p-2 rounded-lg">
              <Mic className="h-6 w-6 text-indigo-400" />
            </div>
            <span className="text-xl font-bold text-white tracking-tight">SpeechToText</span>
          </div>
          <div className="flex items-center gap-6">
            <span className="text-sm font-medium text-slate-400">Welcome, <span className="text-slate-200">{user.name}</span></span>
            <button 
              onClick={logout}
              className="flex items-center gap-2 text-sm text-slate-400 hover:text-red-400 transition-colors"
            >
              <LogOut className="h-4 w-4" /> Logout
            </button>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Upload Section */}
          <div className="lg:col-span-1 space-y-6">
            <div className="bg-slate-900 rounded-2xl p-6 border border-slate-800 shadow-xl">
              <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                <UploadCloud className="h-5 w-5 text-indigo-400" /> Upload Audio
              </h2>
              
              <form onSubmit={handleUpload}>
                <div className="mb-6">
                  <div 
                    className="border-2 border-dashed border-slate-700 rounded-xl p-8 text-center hover:border-indigo-500/50 transition-colors cursor-pointer bg-slate-950/50"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    <FileAudio className="h-10 w-10 text-slate-500 mx-auto mb-3" />
                    <p className="text-sm text-slate-400 mb-1">Click to select an audio file</p>
                    <p className="text-xs text-slate-500">WAV, MP3, FLAC (Max 50MB)</p>
                    <input 
                      type="file" 
                      ref={fileInputRef}
                      onChange={handleFileChange} 
                      className="hidden" 
                      accept="audio/*"
                    />
                  </div>
                  {file && (
                    <div className="mt-4 p-3 bg-indigo-500/10 border border-indigo-500/20 rounded-lg flex items-center justify-between">
                      <span className="text-sm text-indigo-200 truncate pr-4">{file.name}</span>
                      <span className="text-xs text-indigo-400 shrink-0">{(file.size / 1024 / 1024).toFixed(2)} MB</span>
                    </div>
                  )}
                </div>

                <button 
                  type="submit" 
                  disabled={!file || loading}
                  className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 focus:ring-offset-slate-900 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {loading ? `Transcribing... ${uploadProgress}%` : 'Convert to Text'}
                </button>
              </form>
            </div>
          </div>

          {/* History Section */}
          <div className="lg:col-span-2">
            <div className="bg-slate-900 rounded-2xl border border-slate-800 shadow-xl overflow-hidden flex flex-col h-full">
              <div className="p-6 border-b border-slate-800 flex justify-between items-center">
                <h2 className="text-lg font-semibold text-white flex items-center gap-2">
                  <Clock className="h-5 w-5 text-indigo-400" /> Transcription History
                </h2>
                <span className="text-xs font-medium bg-slate-800 text-slate-300 px-3 py-1 rounded-full">
                  {history.length} Records
                </span>
              </div>
              
              <div className="p-6 overflow-y-auto max-h-[600px] flex-1 space-y-4">
                {history.length === 0 ? (
                  <div className="text-center py-12">
                    <FileText className="h-12 w-12 text-slate-700 mx-auto mb-3" />
                    <p className="text-slate-400">No transcriptions yet. Upload an audio file to get started.</p>
                  </div>
                ) : (
                  history.map((item) => (
                    <div key={item.id} className="bg-slate-950/50 border border-slate-800 rounded-xl p-5 hover:border-slate-700 transition-colors">
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <h3 className="text-sm font-medium text-white mb-1">{item.audioFile}</h3>
                          <p className="text-xs text-slate-500">
                            {new Date(item.createdAt).toLocaleString()}
                          </p>
                        </div>
                        <div className="flex gap-2">
                          <button 
                            onClick={() => downloadText(item.transcript, item.audioFile)}
                            className="p-1.5 bg-slate-800 text-indigo-400 rounded-lg hover:bg-slate-700 transition-colors"
                            title="Download Text"
                          >
                            <Download className="h-4 w-4" />
                          </button>
                          <button 
                            onClick={() => handleDelete(item.id)}
                            className="p-1.5 bg-slate-800 text-red-400 rounded-lg hover:bg-slate-700 transition-colors"
                            title="Delete"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                      <div className="bg-slate-900 rounded-lg p-4 max-h-40 overflow-y-auto border border-slate-800/50">
                        <p className="text-sm text-slate-300 whitespace-pre-wrap">{item.transcript}</p>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
};

export default Dashboard;
