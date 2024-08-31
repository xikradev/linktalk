import { useState, useEffect } from 'react';
import axios from 'axios';

const ChatRoom = () => {
    const [publicChat, setPublicChat] = useState([]);
    const [socket, setSocket] = useState(null);
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

    useEffect(() => {
        if (userData.connected) {
            getMessagesByConversation(1);
        }

    }, [userData.connected])

    const getMessagesByConversation = async (conversationId) => {
        const response = await axios.get(`http://localhost:8080/message?conversationId=${conversationId}`)
        console.log(response.data);
        setPublicChat([...response.data]);
    }

    const registerUser = async () => {
        try {
            const response = await axios.post("http://localhost:8080/auth/login", {
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
            const ws = new WebSocket(`ws://localhost:8080/chat/${conversationId}/${token}`);
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
        } catch (error) {
            console.error("Login failed:", error);
            alert("Failed to login. Please check your credentials and try again.");
        }
    };

    const sendMessage = () => {
        if (socket && userData.message.trim()) {
            console.log(userData.message)
            socket.send(userData.message);
            setUserData((prevState) => ({ ...prevState, message: '' }));
        }
    };

    const handleMessageChange = (event) => {
        setUserData((prevState) => ({
            ...prevState,
            message: event.target.value,
        }));
    };

    useEffect(() => {
        console.log(publicChat)
    }, [publicChat])

    return (
        <div className='container'>
            {userData.connected ? (
                <div>
                    <div className="chat-box">
                        {publicChat.map((msg, index) => (
                            <div key={index} className={msg.senderEmail === userData.email ? "message-right" : "message-left"}>
                                {msg.content + ", " + msg.timeSented}
                            </div>
                        ))}
                    </div>
                    <input
                        type="text"
                        placeholder="Type a message..."
                        value={userData.message}
                        onChange={handleMessageChange}
                    />
                    <button type="button" onClick={sendMessage}>
                        Send
                    </button>
                </div>
            ) : (
                <div className='register'>
                    <input
                        id='email'
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
                        placeholder='Senha'
                        value={userData.password}
                        onChange={(e) => setUserData((prevState) => ({
                            ...prevState,
                            password: e.target.value
                        }))}
                    />
                    <button type='button' onClick={registerUser}>Registrar</button>
                </div>
            )}
        </div>
    );
};

export default ChatRoom;
