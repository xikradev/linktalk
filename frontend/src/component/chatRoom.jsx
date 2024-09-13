import { useState, useEffect } from 'react';
import {useLocation} from 'react-router-dom';
import axios from 'axios';
import sendIcon from '/VisualStudio/linktalk/frontend/src/assets/send-message.png';
import newChatIcon from '/VisualStudio/linktalk/frontend/src/assets/chat.png';


const ChatRoom = () => {
    const location = useLocation(); // Access location object
    const { userDataLogin } = location.state || {}; // Get userData from state 
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

    useEffect(() => {
        console.log("User data in ChatRoom:", userDataLogin); 
      }, [userDataLogin]); 

    const getMessagesByConversation = async (conversationId) => {
        const response = await axios.get(`http://localhost:8081/message?conversationId=${conversationId}`)
        console.log(response.data);
        setPublicChat([...response.data]);
    }


    const sendMessage = () => {
        console.log(userData);
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
    {console.log(userDataLogin)}
        <div className='container'>
            {/* <div className="chat-box"> */}
                <div className="member-list">
                    <label>Conversas</label>
                    <button 
                        className="send-button" 
                        onClick={alert("teste")}
                        style={{ marginLeft: '60%'}}
                    > 
                        <img src={newChatIcon} alt='newChatIcon' style={{"textAlign": "center", width: "20px"}}/> </button>
                    <hr />
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
                    <div className="send-message" >
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

            {/* </div> */}
        </div>
    </>
    );
};

export default ChatRoom;
