import { useState, useEffect } from 'react';
import axios from 'axios';
import sendIcon from '/VisualStudio/linktalk/frontend/src/assets/send-message.png'
const ChatRoom = () => {
    const [publicChat, setPublicChat] = useState([]);
    const [socket, setSocket] = useState(null);
    const [tab, setTab] = useState("CHATS");
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
        const response = await axios.get(`http://localhost:8081/message?conversationId=${conversationId}`)
        console.log(response.data);
        setPublicChat([...response.data]);
    }


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
    <>
        <div className='container'>
            <div className="chat-box">
                <div className="member-list">
                    <ul>
                        {/* <li onClick={() => setTab("CHATS")} className={`member ${tab === "CHATS" && "active"}`}>Chats</li> */}
                        {...publicChat.keys().map((name, index)=> {
                            <li onClick={() => setTab(name)} className={`member ${tab === name && "active"}`} key={index}>
                                {name}
                            </li>
                        })} 
                    </ul>
                </div>
                <div className="chat-content">
                    <ul className="chat-messages">
                        {publicChat.map((chat, index) => (
                            <li className={`message ${chat.senderEmail === userData.email ? "self" : ""}`} key={index}>
                              {/* Avatar do remetente */}
                                {chat.senderEmail !== userData.email && (
                                    <div className="avatar">{chat.senderName}</div>
                                )}

                                    {/* Conteúdo da mensagem */}
                                    <div className="message-data">
                                        {chat.content + ", " + chat.timeSented}
                                    </div>

                                {/* Avatar do remetente no caso do próprio usuário */}
                                {chat.senderEmail === userData.email && (
                                    <div className="avatar self">{chat.senderName}</div>
                                )}
                         </li>
                        ))}
                    </ul>
                </div>                     
                <div className="send-message">
                    <input
                         type="text"
                         className="input-message"
                         placeholder="Digite uma mensagem..."
                         value={userData.message}
                         onChange={handleMessageChange}
                    />
                    <button className="send-button" onClick={sendMessage}>
                        <img src={sendIcon} alt='sendIcon' style={{"textAlign": "center"}}/>
                    </button>
                </div>
            </div>
        </div>
    </>
    );
};

export default ChatRoom;
