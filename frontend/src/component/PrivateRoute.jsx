import { Navigate } from 'react-router-dom'; // Usando react-router-dom v6

// eslint-disable-next-line react/prop-types
const PrivateRoute = ({ children }) => {
    const token = localStorage.getItem('token');
    console.log(token);
    return token !== null ? children : <Navigate to="/login" />;
};

export default PrivateRoute;