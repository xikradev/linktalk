/* eslint-disable react/prop-types */
import axios from "axios";
import apiLinkTalk from "../../api/api";


const DeleteMessageModal = ({ setModalOpen, messageId, refreshMessages }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <h3>Deletar Mensagem</h3>
                <hr />
                <div>Tem certeza que voce deseja apagar essa mensagem?</div>

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
                            apiLinkTalk.delete(`/message/${messageId}`).then(() => {
                                refreshMessages();
                            })

                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                    >
                        Sim, Deletar Mensagem
                    </button>
                </div>
            </div>
        </div>
    )
}

export default DeleteMessageModal