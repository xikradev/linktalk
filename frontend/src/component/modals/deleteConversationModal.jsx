/* eslint-disable react/prop-types */
import apiLinkTalk from '../../api/api';

const DeleteConversationModal = ({ setModalOpen, conversationId, refreshMessages }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Deletar Conversa</h3>
                <hr />
                <div>Tem certeza que voce deseja apagar essa conversa?</div>

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
                            console.log(conversationId)
                            apiLinkTalk.delete(`/conversation/${conversationId}`).then(() => {
                                refreshMessages();
                            })

                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Sim, Deletar Conversa
                    </button>
                </div>
            </div>
        </div>
    )
}

export default DeleteConversationModal