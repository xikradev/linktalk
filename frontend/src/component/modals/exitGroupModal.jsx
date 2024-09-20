/* eslint-disable react/prop-types */
import apiLinkTalk from '../../api/api';

const ExitGroupModal = ({ setModalOpen, groupId, userId, refreshMessages }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Sair do grupo</h3>
                <hr />
                <div>Tem certeza que voce deseja sair do grupo?</div>

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
                            apiLinkTalk.put(`/group/${groupId}/remove-user&userIds=${userId}`).then(() => {
                                refreshMessages();
                            })

                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Sim, quero sair do grupo
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ExitGroupModal