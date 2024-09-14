import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import sendIcon from '/img/send-message.png';
import newChatIcon from '/img/chat.png';
import serchIcon from '/img/lupa.png';
import userIcon from '/img/user.png';
import logoutIcon from '/img/logout.png';
import store from '../redux/store';

const ChatRoom = () => {
    const navigate = useNavigate();
    const location = useLocation(); // Access location object
    const { userDataLogin } = location.state || {}; // Get userData from state 
    const [publicChat, setPublicChat] = useState([]);
    const [contacts, setContacts] = useState([])
    const [selectedContact, setSelectedContact] = useState([]);
    const [socket, setSocket] = useState(null);
    const [tab, setTab] = useState("CHATS");
    // modal de pesquisa de usuário
    const [isModalOpen, setModalOpen] = useState(false);
    const [email, setEmail] = useState("");

    const [userData, setUserData] = useState({
        username: '',
        email: '',
        password: '',
        message: '',
        connected: false,
    });

    useEffect(() => {
        console.log(store.getState().socket.socket)
    }, [])

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
        getConversations();
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
        if (store.getState().socket.socket && userData.message.trim()) {
            const newMessage = {
                senderEmail: userData.email,
                senderName: userDataLogin.fullName,
                content: userData.message,
                timeSented: new Date().toLocaleTimeString(),
            };
            store.getState().socket.socket.send(JSON.stringify(newMessage));
            setPublicChat(prevChat => [...prevChat, newMessage]);

            setUserData(prevState => ({ ...prevState, message: '' }));
        }
    };

    const handleMessageChange = (event) => {
        setUserData((prevState) => ({
            ...prevState,
            message: event.target.value,
        }));
    };

    const searchUser = () => {
        console.log(`Buscando usuário com o email: ${email}`);
        closeModal(); // Fecha o modal após a pesquisa
    };

    //Adicionar novo contato e iniciar conversa
    const startConversation = async (user2Id) => {
        const result = await axios.post(`http://localhost:8081/conversation?user1Id=${userDataLogin.id}&user2Id=${user2Id}`);
        console.log(result.data);
        getConversations();
    }

    const getConversations = async () => {
        const result = await axios.get(`http://localhost:8081/user/contactsByUserId/${userDataLogin.id}`);
        console.log(result.data);
        setContacts([...result.data]);
    }


    useEffect(() => {
        console.log(publicChat)
    }, [publicChat])

    return (
        <>
            <div className='container'>
                {/* <div className="chat-box"> */}
                <div className="member-list">
                    <div style={{ "display": "flex", "alignItems": "center" }}>
                        <label>Conversas</label>
                        {/*Abrir a modal para adicionar um novo usuário a lista de contatos*/}
                        <button
                            className="send-button"
                            onClick={() => setModalOpen(true)}
                            style={{ marginLeft: '60%' }}
                        >
                            <img src={newChatIcon} alt='newChatIcon' style={{ "textAlign": "center", width: "20px" }} />
                        </button>
                    </div>
                    <hr />
                    <div className='contact-list'>
                        <ul>
                            {contacts.map((contact, index) => {
                                return (
                                    <li key={index}
                                        onClick={() => {
                                            getMessagesByConversation(contact.conversationId);
                                            setTab(contact.fullName)
                                        }}
                                        className={`member ${tab === contact.fullName && "active"}`} >
                                        <div style={{ "display": "flex", "alignItems": "center" }}>
                                            <img src={userIcon} style={{ marginRight: '10px' }} />
                                            {contact.fullName}
                                        </div>
                                    </li>
                                );
                            })}
                        </ul>
                    </div>
                    <div className='user'>
                        <hr />
                        <div style={{ "display": "flex" }}>
                            <p>Olá, {userDataLogin ? userDataLogin.fullName : ''}</p>
                            <button className="send-button" onClick={() => {
                                setTimeout(() => {
                                    navigate('/');
                                }, 150);
                            }} >
                                <img src={logoutIcon} placeholder='Sair' alt='logoutIcon' style={{ "textAlign": "center" }} />
                            </button>
                        </div>
                    </div>
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
                        <button className="send-button" onClick={() => {
                            setUserData((prevState) => ({ ...prevState, message: '' }));
                            sendMessage()
                        }}>
                            <img src={sendIcon} alt='sendIcon' style={{ "textAlign": "center" }} />
                        </button>
                    </div>
                </div>

                {/* Modal de Pesquisa de Usuário */}
                {isModalOpen && (
                    <div className="modal">
                        <div className="modal-content">
                            <h3>Buscar Usuário</h3>
                            <hr />
                            <input
                                type="email"
                                className="input-message"
                                style={{ marginLeft: '0px', width: '80%' }}
                                placeholder="Digite o email do usuário"
                                value={email}
                                onChange={(e) => {
                                    const filteredEmail = e.target.value.toLowerCase();
                                    setEmail(filteredEmail);
                                }}
                            />
                            <button className="send-button" onClick={() => searchUser}>
                                <img src={serchIcon} alt='serchIcon' style={{ "textAlign": "center" }} />
                            </button>

                            <div className="modal-buttons">
                                {/* Botão de Cancelar */}
                                <button
                                    onClick={() => {
                                        setModalOpen(false);
                                        //closeModal
                                    }}
                                    style={{ padding: '10px 20px', marginRight: '10px', backgroundColor: 'red', color: '#fff', border: 'none', borderRadius: '5px' }}
                                >
                                    Cancelar
                                </button>

                                {/* Botão de Adicionar */}
                                <button
                                    onClick={() => {
                                        //addUser
                                        startConversation(userDataLogin.conversationId);
                                        setModalOpen(false);
                                    }}
                                    style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px' }}
                                >
                                    Adicionar
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                {/* </div> */}
            </div>
        </>
    );
};

export default ChatRoom;
