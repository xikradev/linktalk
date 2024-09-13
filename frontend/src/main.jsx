import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';

// createRoot(document.getElementById('root')).render(
//   <StrictMode>
//     <App />
//   </StrictMode>,
// )
ReactDOM.render(
  <BrowserRouter>
    <App /> 
  </BrowserRouter>,
  document.getElementById('root')
);