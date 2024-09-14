export const SET_SOCKET = 'SET_SOCKET';
export const CLOSE_SOCKET = 'CLOSE_SOCKET';

export const set_socket = (socket) => ({
    type: SET_SOCKET,
    payload: socket,
});

export const closeSocket = () => ({
    type: CLOSE_SOCKET,
});