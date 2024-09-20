/* eslint-disable react/prop-types */
import { useEffect, useState } from "react"
import userIcon from '/img/user.png';
import apiLinkTalk from "../../api/api";

const AddUserGroupModal = ({ setOpenModal, contacts, addUser, groupId }) => {
    const [membersList, setMemberslist] = useState([]);
    const [contactsToBeAdd, setcontactsToBeAdd] = useState([]);

    const getExistedMembers = async () => {
        const response = await apiLinkTalk.get(`/group/${groupId}/members`)
        const list = contacts.filter(con => !response.data.some(members => con.id === members.id));
        setcontactsToBeAdd([...list]);

    }

    useEffect(() => {
        getExistedMembers();
    }, [])


    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Adicionar pessoas no grupo</h3>
                <hr />
                <label style={{ borderBottom: "2px solid purple" }}>contatos</label>
                <div>
                    <ul style={{ height: "200px", overflowY: 'auto' }}>
                        {contactsToBeAdd.length > 0 ?
                            contactsToBeAdd.map((contact, index) => {
                                return (
                                    <li key={index}
                                        onClick={() => {
                                            if (membersList.includes(contact.id)) {
                                                setMemberslist(membersList.filter((item) => item !== contact.id))
                                            } else {
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
                            })
                            :
                            <div style={{ color: "red" }}>Nenhum contato a ser adicionado</div>
                        }
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
                            addUser(membersList, groupId)
                            setOpenModal(false);
                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer', opacity: membersList.length === 0 ? '0.5' : '1' }}
                    >
                        Adicionar ao Grupo
                    </button>
                </div>
            </div>
        </div>
    )
}

export default AddUserGroupModal