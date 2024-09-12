import { useState, useEffect } from 'react';
import axios from 'axios';
import {Link, Navigate} from 'react-router-dom';
import logoLK from '/VisualStudio/linktalk/frontend/src/assets/linktalk.png';
import ChatRoom from './chatRoom';

const login = () => {
    const [publicChat, setPublicChat] = useState([]);
    const [socket, setSocket] = useState(null);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [erroMessage, setErrorMessage] = useState(false);
    const [userData, setUserData] = useState({
        username: '',
        email: '',
        password: '',
        message: '',
        connected: false,
    });

    useEffect(() => {
        // Limpar a conexão WebSocket quando o componente desmontar
        return () => {
            if (socket) {
                socket.close();
            }
        };
    }, [socket]);

    const registerUser = async () => {
        try {
            const response = await axios.post("http://localhost:8081/user/login", {
                email: userData.email,
                password: userData.password
            });

            const token = response.data.token;
            const conversationId = 1; // Defina o conversationId conforme necessário

            setUserData((prevState) => ({
                ...prevState,
                username: response.data.fullName,
            }));

            console.log("Login response:", response.data);

            // Configura o WebSocket
            const ws = new WebSocket(`ws://localhost:8081/chat/${conversationId}/${token}`);
            ws.onopen = () => {
                setUserData((prevState) => ({
                    ...prevState,
                    connected: true,
                }));
                console.log('Connected to WebSocket');
            };

            ws.onmessage = (event) => {
                console.log(event)
                const messageData = JSON.parse(event.data);
                setPublicChat((prevChat) => [...prevChat, messageData]);
            };

            ws.onclose = () => {
                console.log('WebSocket connection closed');
                setUserData((prevState) => ({
                    ...prevState,
                    connected: false,
                }));
            };

            ws.onerror = (error) => {
                console.error('WebSocket error:', error);
            };

            setSocket(ws);
            setIsLoggedIn(true);
        } catch (error) {
            setErrorMessage(true);
            console.error("Login failed:", error);
        }
    };

    return (
        <div className='backgroud_login'> 
            <div className='register'>
                <img src={logoLK}/>
                <h1 style={{"textAlign": "center"}}>Bem Vindo!</h1>
                <form>
                    {erroMessage === true ? 
                    <h5 style={{color: "red"}}>Falha no Login. Verifique suas credenciais e tente novamente.</h5>:
                    <h2></h2>
                }
                <input
                    id='email'
                    className="input-login"
                    type='email'
                    placeholder='Email'
                    value={userData.email}
                    onChange={(e) => setUserData((prevState) => ({
                        ...prevState,
                        email: e.target.value
                    }))}
                />
                <input
                    id='password'
                    type='password'
                    className="input-login"
                    style={{marginTop: "10px"}}
                    placeholder='Senha'
                    value={userData.password}
                    onChange={(e) => {
                        setUserData((prevState) => ({
                        ...prevState,
                        password: e.target.value
                    }));
                    setErrorMessage(false);
                }}
                />
                <button className="button-login" type='button' onClick={registerUser}>Login</button>
                <Link style={{display: "block", textAlign: "center", marginTop: "10px" }} 
                    to="/cadastro">
                Não tem uma conta? Cadastre-se
                </Link>
            </form>
        </div>
    {isLoggedIn && <Navigate to="/chatRoom" replace={true}/>}
    </div>
);
}
export default login;