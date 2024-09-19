/* eslint-disable react/prop-types */
import { useState } from "react"
import userIcon from '/img/user.png';


const CreateGroupModal = ({ setOpenModal, contacts, createGroup }) => {
    const [groupName, setGroupName] = useState('')
    const [tab, setTab] = useState('');
    const [membersList, setMemberslist] = useState([]);

    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Criar Grupo</h3>
                <hr />
                <label >Nome do Grupo</label>
                <input
                    type="teste"
                    className="input-message"
                    style={{ marginLeft: '0px', width: '90%', marginTop: "8px" }}
                    placeholder="Digite o nome do Grupo"
                    value={groupName}
                    onChange={(e) => {
                        setGroupName(e.target.value)
                    }}
                />
                <div>
                    <ul style={{ height: "200px", overflowY: 'auto' }}>
                        {contacts.map((contact, index) => {
                            return (
                                <li key={index}
                                    onClick={() => {
                                        setTab(contact.email)
                                        if (membersList.includes(contact.id)) {
                                            setMemberslist(membersList.filter((item) => item !== contact.id))
                                        } else {
                                            membersList.push(contact.id)
                                        }

                                    }}
                                    className={`member ${membersList.includes(contact.id) && "active"}`} >
                                    <div style={{ "display": "flex", "alignItems": "center" }}>
                                        <img src={userIcon} style={{ marginRight: '10px' }} />
                                        {contact.fullName}
                                    </div>
                                </li>
                            );
                        })}
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
                        disabled={groupName === '' || membersList.length === 0}
                        onClick={() => {
                            createGroup(membersList, groupName)
                            setOpenModal(false);
                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer', opacity: (groupName === '' || membersList.length === 0) ? '0.5' : '1' }}
                    >
                        Criar Grupo
                    </button>
                </div>
            </div>
        </div>
    )
}

export default CreateGroupModal