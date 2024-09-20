/* eslint-disable react/prop-types */

import { useState } from "react";

const EditNameGroupModel = ({ setOpenModal, updateName, groupId, originalName }) => {
    const [groupName, setGroupName] = useState(originalName)

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
                        disabled={groupName === ''}
                        onClick={() => {
                            updateName(groupName, groupId)
                            setOpenModal(false);
                        }}
                        style={{ padding: '10px 20px', backgroundColor: 'green', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer', opacity: groupName === '' ? '0.5' : '1' }}
                    >
                        Atualizar
                    </button>
                </div>
            </div>
        </div>
    )
}

export default EditNameGroupModel