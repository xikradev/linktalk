/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */

import { useEffect, useState } from "react";
import apiLinkTalk from "../../api/api";
import userIcon from '/img/user.png';


const RemoveUserGroups = ({ setOpenModal, removeUser, groupId, userId }) => {
    const [membersList, setMemberslist] = useState([]);
    const [contactsToBeRemove, setcontactsToBeRemove] = useState([]);

    const getExistedMembers = async () => {
        const response = await apiLinkTalk.get(`/group/${groupId}/members`)
        setcontactsToBeRemove(response.data.filter(item => item.id !== userId));

    }

    useEffect(() => {
        getExistedMembers();
    }, [])
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Remover pessoas no grupo</h3>
                <hr />
                <label style={{ borderBottom: "2px solid purple" }}>contatos</label>
                <div>
                    <ul style={{ height: "200px", overflowY: 'auto' }}>
                        {contactsToBeRemove.length > 0 ? contactsToBeRemove.map((contact, index) => {
                            return (
                                <li key={index}
                                    onClick={() => {
                                        if (membersList.includes(contact.id)) {
                                            setMemberslist(membersList.filter((item) => item !== contact.id))
                                        } else {
                                            console.log(contact)
                                            setMemberslist([...membersList, contact.id]);
                                        }

                                    }}
                                    className={`member ${membersList.includes(contact.id) && "active"}`} >
                                    <div style={{ "display": "flex", "alignItems": "center" }}>
                                        <img src={userIcon} style={{ marginRight: '10px' }} />
                                        {contact.fullName}
                                    </div>
                                </li>
                            );
                        }) : <div style={{ color: "red" }}>Nenhum membro para remover</div>}
                    </ul>
                </div>

                <div className="modal-buttons">
                    {/* Botão de Cancelar */}
                    <button
                        onClick={() => {
                            setOpenModal(false);
                            //closeModal
                        }}
                        style={{ padding: '10px 20px', marginRight: '10px', backgroundColor: 'red', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Cancelar
                    </button>

                    {/* Botão de Adicionar */}
                    <button
                        disabled={membersList.length === 0}
                        onClick={() => {
                            removeUser(membersList, groupId)
                            setOpenModal(false);
                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer', opacity: membersList.length === 0 ? '0.5' : '1' }}
                    >
                        Remover do Grupo
                    </button>
                </div>
            </div>
        </div>
    )
}

export default RemoveUserGroups