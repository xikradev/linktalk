import { SET_SOCKET, CLOSE_SOCKET } from './socketActions';

const initialState = {
    socket: null,
};

const socketReducer = (state = initialState, action) => {
    switch (action.type) {
        case SET_SOCKET:
            return { ...state, socket: action.payload };
        case CLOSE_SOCKET:
            return { ...state, socket: null };
        default:
            return state;
    }
};

export default socketReducer;