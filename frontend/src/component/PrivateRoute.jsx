import { Navigate } from 'react-router-dom'; // Usando react-router-dom v6

// eslint-disable-next-line react/prop-types
const PrivateRoute = ({ children }) => {
    const user = sessionStorage.getItem('user');

   // console.log(user);
    return user !== null ? children : <Navigate to="/login" />;
};

export default PrivateRoute;