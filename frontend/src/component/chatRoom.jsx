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
import ConfigIcon from '/img/config.png';
import imageIcon from '/img/imageIcon.png';
import LinkTalk from '/img/linktalk.png';
import GroupAdd from '/img/groupAdd.png';
import DeleteMessageModal from './modals/deleteMessageModal';
import apiLinkTalk from '../api/api.js';
import CreateGroupModal from './modals/createGroupModal.jsx';
import DeleteConversationModal from './modals/deleteConversationModal.jsx';
import ExitGroupModal from './modals/exitGroupModal.jsx';
import AddUserGroupModal from './modals/addUserGroupModal.jsx';
import RemoveUserGroups from './modals/removeUserGroups.jsx';
import EditNameGroupModel from './modals/editNameGroupModel.jsx';
import DeleteGroupModal from './modals/deleteGroupModal.jsx';


const ChatRoom = () => {
    const navigate = useNavigate();
    const scrollContainerRef = useRef();

    const [publicChat, setPublicChat] = useState([]);
    const [contacts, setContacts] = useState([])
    const [selectedContact, setSelectedContact] = useState(null);
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [socket, setSocket] = useState(null);
    const [tab, setTab] = useState("CHATS");
    const [isOpenDeleteConversationModal, setIsOpenDeleteConversationModal] = useState(false);
    const [openEditMessage, setOpenEditMessage] = useState(null);
    const [openDeleteMessageModal, setOpenDeleteMessageModal] = useState(false);
    const [selectedMessageId, setSelectedMessageId] = useState(null);
    const [selectedImage, setSelectedImage] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [isContactList, setIsContactList] = useState(true);
    const [groups, setGroups] = useState([]);
    const [isOpenGroup, setIsOpenGroup] = useState(false);
    const [configContact, setConfigContact] = useState(null);
    const [foundedUser, setFoundedUser] = useState(null);
    const [notFoundedUser, setNotFoundedUser] = useState(false);
    const [isOpenGroupAdd, setIsOpenGroupAdd] = useState(false);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [configGroup, setConfigGroup] = useState(null);

    const [isOpenExitGroup, setIsOpenExitGroup] = useState(false);
    const [isOpenAddUSerGroup, setIsOpenAddUSerGroup] = useState(false);
    const [isOpenRemoveUSerGroup, setIsOpenRemoveUSerGroup] = useState(false);
    const [isOpenEditGroupName, setIsOpenEditGroupName] = useState(false);
    const [isOpenDeleteGroup, setIsOpeDeleteGroup] = useState(false);
    // modal de pesquisa de usuário
    const [isModalOpen, setModalOpen] = useState(false);
    const [email, setEmail] = useState("");
    const [userData, setUserData] = useState({
        id: null,
        username: '',
        email: '',
        message: '',
        connected: false,
        token: ''
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
        if (scrollContainerRef.current !== null) {
            console.log(scrollContainerRef.current?.scrollTop, scrollContainerRef.current?.scrollHeight)
            scrollContainerRef.current.scrollTop = scrollContainerRef.current.scrollHeight;
        }
    };

    useEffect(() => {
        setTimeout(() => {
            scrollToBottom();
        }, 150) // Garantir que o scroll esteja no final ao montar o componente
    }, [selectedContact, selectedGroup]);

    useEffect(() => {
        const user = JSON.parse(sessionStorage.getItem("user"));

        setUserData({
            id: user.id,
            username: user.fullName,
            email: user.email,
            message: '',
            connected: false,
            token: user.token
        })
    }, [])

    useEffect(() => {
        if (selectedContact !== null || selectedGroup !== null) {
            const ws = new WebSocket(`ws://localhost:8081/${isContactList ? "conversation" : "group"}/${isContactList ? selectedContact?.conversationId : selectedGroup.id}/${userData.token}`);
            ws.onopen = () => {
                setUserData((prevState) => ({
                    ...prevState,
                    connected: true,
                }));
                console.log('Connected to WebSocket');
            };

            ws.onmessage = (event) => {
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
        }
    }, [selectedContact, selectedGroup])

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
            getGroups();
        }
    }, [userData])


    useEffect(() => {
        console.log("User data in ChatRoom:", userData);
    }, [userData]);

    //capturar o evento da tela enter
    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
          event.preventDefault(); 
          sendMessage();
        }
    };

    const getMessagesByConversation = async (conversationId) => {
        const response = await apiLinkTalk.get(`/message/conversation/${conversationId}`)
        setPublicChat([...response.data]);
    }

    const getMessagesByGroup = async (groupId) => {
        const response = await apiLinkTalk.get(`/message/group/${groupId}`)
        setPublicChat([...response.data]);
    }


    const sendMessage = () => {
        if (socket && (userData.message.trim() || selectedImage)) {
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

    const searchUser = async (searchEmail) => {
        const response = await apiLinkTalk.get(`/user/email/${searchEmail}`)
            .then((res) => { setFoundedUser(res.data); setNotFoundedUser(false) })
            .catch(() => setNotFoundedUser(true));
        setFoundedUser(response.data)
    };

    useEffect(() => {
        // Função que será chamada quando houver clique na tela
        const handleClick = () => {
            if (openEditMessage !== null) {
                setOpenEditMessage(null)

            }
            if (configContact !== null) {
                setConfigContact(null)
            }
            if (configGroup !== null) {
                setConfigGroup(null)
            }
            // Alterna o estado a cada clique
        };

        // Adiciona o listener de clique no documento
        document.addEventListener('click', handleClick);

        // Cleanup: Remove o listener quando o componente for desmontado
        return () => {
            document.removeEventListener('click', handleClick);
        };
    }, [openEditMessage, configContact, configGroup]); // [] garante que o evento será adicionado apenas uma vez, na montagem do componente


    //Adicionar novo contato e iniciar conversa
    const startConversation = async (user2Id) => {
        const result = await apiLinkTalk.post(`/conversation?user1Id=${userData.id}&user2Id=${user2Id}`);
        getConversations();
    }

    //Criar novo grupo
    const createGroup = async (userIds, groupName) => {
        const queryString = userIds.map(id => `userIds=${id}`).join('&')
        const result = await apiLinkTalk.post(`/group?${queryString}`, { name: groupName });
        getGroups();
    }

    const getConversations = async () => {
        const result = await apiLinkTalk.get(`/user/contactsByUserId/${userData.id}`);
        setContacts([...result.data]);
    }

    const getGroups = async () => {
        const result = await apiLinkTalk.get(`/user/${userData.id}/groups`);
        setGroups([...result.data]);
    }

    const handleFileChange = (event) => {
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
                    <div style={{ display: "flex", flexDirection: "row", justifyContent: "end" }}>
                            {/*Abrir modal de adicionar novo usuario*/}
                            <button
                                className="send-button"
                                onClick={() => setModalOpen(true)}
                            >
                                <img src={newChatIcon} alt='newChatIcon' style={{ "textAlign": "center", width: "20px" }} />
                            </button>
                            {/*Abrir modal de criar grupo*/}
                            <button
                                className="send-button"
                                onClick={() => setIsOpenGroupAdd(true)}
                            >
                                <img src={GroupAdd} alt='newChatIcon' style={{ "textAlign": "center", width: "30px" }} />
                            </button>
                    </div>
                    {/*Alterar entre contatos e grupos*/}
                    <div style={{ "display": "flex", "alignItems": "center", justifyContent: "space-around" }}>
                        <label style={{ cursor: "pointer", borderBottom: isContactList && "2px solid purple" }} onClick={() => {
                            setSelectedContact(null);
                           setIsContactList(true);
                            setIsOpenGroup(false);
                            setSelectedGroup(null);
                            }}>Conversas</label>
                        <label style={{ cursor: "pointer", borderBottom: !isContactList && "2px solid purple" }} onClick={() => {
                            setSelectedContact(null);
                            setIsContactList(false);
                            setIsOpenGroup(true);
                        }}>Grupo</label>
                    </div>
                    <hr />
                    {/*Area dos contatos*/}
                    <div className='contact-list'>
                        {isContactList ? (<ul>
                            {contacts.map((contact, index) => {
                                return (
                                    <li key={index}
                                        onClick={() => {
                                            getMessagesByConversation(contact.conversationId);
                                            setTab(contact.email)
                                            setSelectedContact(contact);
                                        }}
                                        className={`member ${tab === contact.email && "active"}`} >
                                        <div style={{ "display": "flex", "alignItems": "center", justifyContent: "space-between", position: "relative" }}>
                                            <div style={{ "display": "flex", "alignItems": "center" }}>
                                                <img src={userIcon} style={{ marginRight: '10px' }} />
                                                {contact.fullName}
                                            </div>
                                            {configContact === contact.id && (<div className='edit-contact' style={{
                                                position: 'absolute', width: 'auto', background: '#f0f0f0', right: 2, top: -30, backgroundColor: '#FFF',
                                                color: 'black',
                                                boxShadow: '0 3px 10px rgb(0 0 0 / 0.2)'
                                            }}>
                                                <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setSelectedConversation(contact.conversationId); setIsOpenDeleteConversationModal(true); }}>Remover Conversa</p>
                                            </div>)}
                                            <img src={ConfigIcon} style={{ marginLeft: '10px', width: "25px" }} onClick={() => setConfigContact(contact.id)} />
                                        </div>
                                    </li>
                                );
                            })}
                        </ul>) : (
                            <ul>
                                {groups.map((group, index) => {
                                    return (
                                        <li key={index}
                                            onClick={() => {
                                                getMessagesByGroup(group.id)
                                                setTab(group.id)
                                                setSelectedGroup(group)
                                            }}
                                            className={`member ${tab === group.id && "active"}`} >
                                            <div style={{ "display": "flex", "alignItems": "center", justifyContent: "space-between", position: "relative" }}>
                                                <div style={{ "display": "flex", "alignItems": "center" }}>
                                                    <img src={userIcon} style={{ marginRight: '10px' }} />
                                                    {group.name}
                                                </div>
                                                {configGroup === group.id && (<div className='edit-contact' style={{
                                                    position: 'absolute', width: 'auto', background: '#f0f0f0', zIndex: "5", right: 2, top: -30, backgroundColor: '#FFF',
                                                    color: 'black',
                                                    boxShadow: '0 3px 10px rgb(0 0 0 / 0.2)'
                                                }}>
                                                    {group.admin ?
                                                        <>
                                                            <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => setIsOpenAddUSerGroup(true)}>Adicionar pessoas</p>
                                                            <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => setIsOpenRemoveUSerGroup(true)}>Remover pessoas</p>
                                                            <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setIsOpenEditGroupName(true) }}>Editar Nome</p>
                                                            <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setIsOpeDeleteGroup(true) }}><span style={{ color: "red" }}>Apagar grupo</span></p>

                                                        </>
                                                        :
                                                        <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setIsOpenExitGroup(true) }}>Sair do Grupo</p>}
                                                </div>)}
                                                <img src={ConfigIcon} style={{ marginLeft: '10px', width: "25px" }} onClick={() => setConfigGroup(group.id)} />
                                            </div>
                                        </li>
                                    );
                                })}
                            </ul>
                        )}
                    </div>
                    {/*Area do Usuarior Logado*/}
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
                    {(selectedContact !== null || selectedGroup !== null) &&
                        <><ul className="chat-messages" ref={scrollContainerRef} >
                            {publicChat.map((chat, index) => (
                                <li className={`message ${chat.senderEmail === userData.email ? "self" : ""}`} key={index}>
                                    <div style={{ position: 'relative' }}>
                                        {/* Mensagens, se é de destinatario ou remetente */}
                                        <div className='message-content' style={chat.senderEmail === userData.email ? {backgroundColor: "#90ebb0"} : {backgroundColor: "#90b9eb"}}>
                                            {/* Identificação de destinatario em grupo */}
                                            {!isContactList && chat.senderEmail !== userData.email && <div style={{ color: "rgb(68, 68, 68)", textAlign: "start", marginBottom: "8px" }}>{chat.senderEmail}</div>}
                                            <div style={{ display: 'flex', flexDirection: 'row' }}>
                                                {chat.senderEmail !== userData.email && (
                                                    <div className="avatar">{chat.senderName}</div>

                                                )}

                                                {/* Conteúdo da mensagem */}
                                                <div className="message-data">
                                                    {(chat?.imgUrl !== undefined) ? <img style={chat?.imgUrl !== undefined ? { border: "1px solid gray", borderRadius: "5px" } : { border: 'none', borderRadius: "0px" }} src={chat.imgUrl}></img> : null}
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
                                                            position: 'absolute', width: 'auto', background: 'green', right: 2, top: -20, backgroundColor: '#FFF',
                                                            color: 'black',
                                                            boxShadow: '0 3px 10px rgb(0 0 0 / 0.2)'
                                                        }}>
                                                            <p style={{ margin: '0px', textAlign: 'center', padding: '6px 12px', cursor: 'pointer' }} onClick={() => { setOpenDeleteMessageModal(true); setSelectedMessageId(chat.id) }}>Deletar</p>
                                                        </div>}
                                                        <div className="avatar self">{chat.senderName}</div>
                                                    </>
                                                )}
                                            </div>
                                            <div className={`message-time ${chat.senderEmail === userData.email ? "self" : ""}`}>{chat.timeSented}</div>
                                        </div>
                                    </div>

                                </li>
                            ))}
                        </ul>
                        {/*Selecionar imagem para envio e enviar mensagem */}
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
                                        onKeyDown={handleKeyDown}
                                    />
                                    <button className="send-button" onClick={() => {
                                        setUserData((prevState) => ({ ...prevState, message: '' }));
                                        sendMessage()
                                    }}>
                                        <img src={sendIcon} alt='sendIcon' style={{ "textAlign": "center" }} />
                                    </button>
                                </div>
                            </div>
                        </> 
                    }
                    {(isOpenGroup === false && selectedContact === null && selectedGroup === null) &&
                        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: '100%', flexDirection: "column" }}>
                            <img src={LinkTalk}></img>
                            <p style={{ fontWeight: 'bolder', fontSize: "25px" }}>Vamos Conversar!!</p>
                            <span style={{ marginTop: "-20px" }}>Clique em uma conversa para iniciar</span>
                        </div>}
                    {(isOpenGroup === true && selectedContact === null &&  selectedGroup === null) &&
                        <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: '100%', flexDirection: "column" }}>
                            <img src={LinkTalk}></img>
                            <p style={{ fontWeight: 'bolder', fontSize: "25px" }}>Vamos Conversar!!</p>
                            <span style={{ marginTop: "-20px" }}>Clique em um grupo para iniciar</span>
                        </div>}
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
                                    setFoundedUser(null)
                                    setNotFoundedUser(false);
                                    const filteredEmail = e.target.value.toLowerCase();
                                    setEmail(filteredEmail);
                                }}
                            />
                            <button className="send-button" onClick={() => searchUser(email)}>
                                <img src={serchIcon} alt='serchIcon' style={{ "textAlign": "center" }} />
                            </button>

                            {foundedUser && (<div style={{
                                "display": "flex", "alignItems": "center", "padding": "12px", border: "1px solid black", marginTop: "10px", borderRadius: "5px"
                            }}>
                                <img src={userIcon} style={{ marginRight: '10px' }
                                } />
                                {foundedUser.fullName}
                            </div>)}
                            {foundedUser === null && notFoundedUser && (
                                <div style={{ color: "red" }}>Não foi possível encontrar nenhum usuário com esse email</div>
                            )}

                            <div className="modal-buttons">
                                {/* Botão de Cancelar */}
                                <button
                                    onClick={() => {
                                        setFoundedUser(null);
                                        setModalOpen(false);
                                        //closeModal
                                    }}
                                    style={{ cursor: 'pointer', padding: '10px 20px', marginRight: '10px', backgroundColor: 'red', color: '#fff', border: 'none', borderRadius: '5px' }}
                                >
                                    Cancelar
                                </button>

                                {/* Botão de Adicionar */}
                                <button
                                    disabled={foundedUser === null}
                                    onClick={() => {
                                        //addUser
                                        startConversation(foundedUser.id);
                                        setModalOpen(false);
                                    }}
                                    style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', cursor: "pointer", borderRadius: '5px', opacity: foundedUser ? 1 : 0.5 }}
                                >
                                    Adicionar
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                {/* </div> */}
                {openDeleteMessageModal && <DeleteMessageModal setModalOpen={(e) => setOpenDeleteMessageModal(e)} messageId={selectedMessageId} refreshMessages={async () => {
                    const response = await apiLinkTalk.get(`/message/${isContactList ? 'conversation' : 'group'}/${isContactList ? selectedContact.conversationId : selectedGroup.id}`)
                    setPublicChat(response.data);
                }} />}

                {isOpenDeleteConversationModal && <DeleteConversationModal setModalOpen={(e) => setIsOpenDeleteConversationModal(e)} conversationId={selectedConversation} refreshMessages={async () => {
                    getConversations();
                    setSelectedContact(null);
                }} />}

                {isOpenExitGroup && <ExitGroupModal setModalOpen={(e) => setIsOpenExitGroup(e)} groupId={selectedGroup.id} userId={userData.id} refreshMessages={async () => {
                    getGroups();
                    setSelectedGroup(null);
                }} />}
                {isOpenAddUSerGroup && <AddUserGroupModal contacts={contacts} groupId={selectedGroup?.id} setOpenModal={(e) => setIsOpenAddUSerGroup(e)} addUser={async (memberList, groupId) => {
                    const queryString = memberList.map(id => `userIds=${id}`).join('&')
                    await apiLinkTalk.put(`/group/${groupId}/add-user?${queryString}`);
                    getGroups();
                }} />}
                {isOpenRemoveUSerGroup && <RemoveUserGroups groupId={selectedGroup?.id} userId={userData.id} setOpenModal={(e) => setIsOpenRemoveUSerGroup(e)} removeUser={async (memberList, groupId) => {
                    const queryString = memberList.map(id => `userIds=${id}`).join('&')
                    await apiLinkTalk.put(`/group/${groupId}/remove-user?${queryString}`);
                    getGroups();
                }} />}
                {isOpenEditGroupName && <EditNameGroupModel groupId={selectedGroup?.id} setOpenModal={(e) => { setIsOpenEditGroupName(e) }} originalName={selectedGroup?.name} updateName={async (groupName, groupId) => {
                    await apiLinkTalk.put(`/group/${groupId}/updateName`, {
                        name: groupName
                    });
                    getGroups();
                    setTab(selectedGroup.id)
                }} />}
                {isOpenDeleteGroup && <DeleteGroupModal groupId={selectedGroup?.id} setModalOpen={(e) => { setIsOpeDeleteGroup(e) }} refreshMessages={() => {
                    getGroups();
                    setSelectedGroup(null);
                }} />}
                {isOpenGroupAdd && <CreateGroupModal contacts={contacts} setOpenModal={(e) => setIsOpenGroupAdd(e)} createGroup={createGroup} />}
            </div >
        </>
    );
};

export default ChatRoom;
