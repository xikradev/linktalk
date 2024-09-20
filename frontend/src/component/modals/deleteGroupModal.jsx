/* eslint-disable react/prop-types */
import React from 'react'
import apiLinkTalk from '../../api/api';

const DeleteGroupModal = ({ setModalOpen, groupId, refreshMessages }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Apagar grupo</h3>
                <hr />
                <div>Tem certeza que voce deseja apagar o grupo?</div>

                <div className="modal-buttons">
                    {/* Botão de Cancelar */}
                    <button
                        onClick={() => {
                            setModalOpen(false);
                            //closeModal
                        }}
                        style={{ padding: '10px 20px', marginRight: '10px', backgroundColor: 'red', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Cancelar
                    </button>

                    {/* Botão de Adicionar */}
                    <button
                        onClick={() => {
                            setModalOpen(false);
                            apiLinkTalk.delete(`/group/${groupId}`).then(() => {
                                refreshMessages();
                            })

                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Sim, quero apagar o grupo
                    </button>
                </div>
            </div>
        </div>
    )
}

export default DeleteGroupModal