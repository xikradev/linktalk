import { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import sendIcon from '/img/send-message.png';
import newChatIcon from '/img/chat.png';
import serchIcon from '/img/lupa.png';
import userIcon from '/img/user.png';
import logoutIcon from '/img/logout.png';
import store from '../redux/store';
import ArrowEditIcon from '/img/seta-edit.png';
import imageIcon from '/img/imageIcon.png';
import DeleteMessageModal from './modals/deleteMessageModal';

const ChatRoom = () => {
    const navigate = useNavigate();
    const [publicChat, setPublicChat] = useState([]);
    const [contacts, setContacts] = useState([])
    const [selectedContact, setSelectedContact] = useState(null);
    const [socket, setSocket] = useState(null);
    const [tab, setTab] = useState("CHATS");
    const scrollContainerRef = useRef();
    const [openEditMessage, setOpenEditMessage] = useState(null);
    const [openDeleteMessageModal, setOpenDeleteMessageModal] = useState(false);
    const [selectedMessageId, setSelectedMessageId] = useState(null);
    const [selectedImage, setSelectedImage] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    // modal de pesquisa de usuário
    const [isModalOpen, setModalOpen] = useState(false);
    const [email, setEmail] = useState("");
    const [userData, setUserData] = useState({
        id: null,
        username: '',
        email: '',
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

    const scrollToBottom = () => {
        console.log(scrollContainerRef.current)
        if (scrollContainerRef.current !== null) {
            console.log(scrollContainerRef.current?.scrollTop, scrollContainerRef.current?.scrollHeight)
            scrollContainerRef.current.scrollTop = scrollContainerRef.current.scrollHeight;
        }
    };

    useEffect(() => {
        setTimeout(() => {
            scrollToBottom();
        }, 150) // Garantir que o scroll esteja no final ao montar o componente
    }, [selectedContact]);

    useEffect(() => {
        setUserData({
            id: store.getState().user.user.id,
            username: store.getState().user.user.username,
            email: store.getState().user.user.email,
            message: '',
            connected: false,
        })
    }, [store.getState().user.user])

    useEffect(() => {
        console.log(selectedContact)
        if (selectedContact !== null) {
            console.log("websocket")
            const ws = new WebSocket(`ws://localhost:8081/chat/${selectedContact?.conversationId}/${localStorage.getItem("token")}`);
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

            console.log(ws)

            setSocket(ws);
        }
    }, [selectedContact])

    useEffect(() => {
        // Limpar a conexão WebSocket quando o componente desmontar
        return () => {
            if (socket) {
                socket.close();
            }
        };
    }, [socket]);

    useEffect(() => {
        if (userData.id !== null) {
            getConversations();
        }
    }, [userData])


    useEffect(() => {
        console.log("User data in ChatRoom:", userData);
    }, [userData]);

    const getMessagesByConversation = async (conversationId) => {
        console.log(conversationId)
        const response = await axios.get(`http://localhost:8081/message?conversationId=${conversationId}`)
        console.log(response.data);
        setPublicChat([...response.data]);
    }


    const sendMessage = () => {
        console.log(userData);
        if (socket && userData.message.trim()) {
            const newMessage = {
                senderEmail: userData.email,
                senderName: userData.username,
                content: userData.message,
                timeSented: new Date().toLocaleTimeString(),
            };
            socket.send(JSON.stringify({ text: newMessage.content, image: selectedImage }));

            setUserData(prevState => ({ ...prevState, message: '' }));
            setSelectedImage(null);
            setImagePreview(null);
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

    useEffect(() => {
        // Função que será chamada quando houver clique na tela
        const handleClick = () => {
            if (openEditMessage !== null) {
                setOpenEditMessage(null)
            }
            // Alterna o estado a cada clique
        };

        // Adiciona o listener de clique no documento
        document.addEventListener('click', handleClick);

        // Cleanup: Remove o listener quando o componente for desmontado
        return () => {
            document.removeEventListener('click', handleClick);
        };
    }, [openEditMessage]); // [] garante que o evento será adicionado apenas uma vez, na montagem do componente


    //Adicionar novo contato e iniciar conversa
    const startConversation = async (user2Id) => {
        const result = await axios.post(`http://localhost:8081/conversation?user1Id=${userData.id}&user2Id=${user2Id}`);
        console.log(result.data);
        getConversations();
    }

    const getConversations = async () => {
        const result = await axios.get(`http://localhost:8081/user/contactsByUserId/${userData.id}`);
        console.log(result.data);
        setContacts([...result.data]);
    }


    useEffect(() => {
        console.log(publicChat)
    }, [publicChat])

    const handleFileChange = (event) => {
        console.log(event.target.value)
        const selectedFile = event.target.files[0];
        const reader = new FileReader();
        reader.onloadend = () => {
            setSelectedImage(reader.result.split(",")[1]); // Base64 encoded image
        };
        reader.readAsDataURL(selectedFile);

        // Gerar uma URL de pré-visualização
        if (selectedFile) {
            const previewUrl = URL.createObjectURL(selectedFile);
            setImagePreview(previewUrl);
        }
        event.target.value = "";
    };

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
                                            setSelectedContact(contact);
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
                            <p>Olá, {userData ? userData.username : ''}</p>
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
                    {selectedContact !== null ? (<ul className="chat-messages" ref={scrollContainerRef} >
                        {publicChat.map((chat, index) => (
                            <li className={`message ${chat.senderEmail === userData.email ? "self" : ""}`} key={index}>
                                <div style={{ position: 'relative' }}>

                                    <div className='message-content'>{/* Avatar do remetente */}
                                        <div style={{ display: 'flex', flexDirection: 'row' }}>
                                            {chat.senderEmail !== userData.email && (
                                                <div className="avatar">{chat.senderName}</div>

                                            )}

                                            {/* Conteúdo da mensagem */}
                                            <div className="message-data">
                                                {chat?.imgUrl !== null && <img src={chat.imgUrl}></img>}
                                                <span>{chat.content}</span>
                                            </div>

                                            {/* Avatar do remetente no caso do próprio usuário */}
                                            {chat.senderEmail === userData.email && (
                                                <>

                                                    <img className="arrow-edit" src={ArrowEditIcon} width={20} height={20} onClick={() => {
                                                        if (openEditMessage === index) {
                                                            setOpenEditMessage(null)
                                                        } else {
                                                            setOpenEditMessage(index)
                                                        }
                                                    }} />
                                                    {openEditMessage === index && <div className='edit-message' style={{
                                                        position: 'absolute', width: 'auto', background: 'green', right: 2, top: -50, backgroundColor: '#FFF',
                                                        color: 'black',
                                                        boxShadow: '0 3px 10px rgb(0 0 0 / 0.2)'
                                                    }}>
                                                        <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setOpenDeleteMessageModal(true); setSelectedMessageId(chat.id) }}>Deletar</p>
                                                    </div>}
                                                    <div className="avatar self">{chat.senderName}</div>
                                                </>
                                            )}
                                        </div>
                                        <div style={{ display: 'flex', justifyContent: 'end' }}>{chat.timeSented}</div>
                                    </div>
                                </div>

                            </li>
                        ))}
                    </ul>) : <div>teste</div>}

                    <div className="send-message" >
                        {imagePreview && <div className='image-preview'><img src={imagePreview} width={30} height={30} alt="" /><span onClick={() => {
                            setSelectedImage(null);
                            setImagePreview(null);
                        }} style={{ marginTop: "-30px", paddingLeft: '5px', cursor: "pointer" }}>x</span></div>}
                        <div style={{
                            display: "flex",
                            flexDirection: 'row',
                            width: "100%"
                        }}>
                            <input type="file" id="imageInput" name="file" accept="image/*" style={{ display: 'none' }} onChange={handleFileChange}></input>
                            <button className='add-image' onClick={() => document.getElementById("imageInput").click()}> <img src={imageIcon} alt="" width={30} /></button>


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
                                        startConversation(userData.conversationId);
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
                {openDeleteMessageModal && <DeleteMessageModal setModalOpen={(e) => setOpenDeleteMessageModal(e)} messageId={selectedMessageId} refreshMessages={async () => {
                    const response = await axios.get(`http://localhost:8081/message?conversationId=${selectedContact.conversationId}`)
                    setPublicChat(response.data);
                }} />}
            </div >
        </>
    );
};

export default ChatRoom;
