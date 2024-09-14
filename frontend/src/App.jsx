import ChatRoom from './component/chatRoom'
import Login from './component/login'
import Cadastro from './component/cadastro'
import { Route, Routes } from 'react-router-dom';
import PrivateRoute from './component/PrivateRoute';
function App() {

  return (
    <>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/chatRoom" element={<PrivateRoute><ChatRoom /></PrivateRoute>} />

      </Routes>
    </>
  )
}

export default App
