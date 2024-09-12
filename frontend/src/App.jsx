import ChatRoom from './component/chatRoom'
import Login from './component/login'
import Cadastro from './component/cadastro'
import { BrowserRouter, Route, Routes } from 'react-router-dom';
function App() {

  return (
    <>
     <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route path="/chatRoom" element={<ChatRoom />} />
      
      </Routes>
    </>
  )
}

export default App
