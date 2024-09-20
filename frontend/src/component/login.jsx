/* eslint-disable react-hooks/rules-of-hooks */
import { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import logoLK from '/img/linktalk.png';
import VisibleIcon from '/img/olho.png';
import NoVisibleIcon from '/img/naoVisivel.png';
import { useDispatch } from 'react-redux';
import { userLogin } from '../redux/userActions';

const login = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [erroMessage, setErrorMessage] = useState(false);
    const [userData, setUserData] = useState({
        username: '',
        email: '',
        password: '',
        message: '',
        connected: false,
    });
    const [showPassword, setShowPassword] = useState(false);

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword); // Alterna entre mostrar e esconder a senha
    };

     //capturar o evento da tela enter
    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
          event.preventDefault(); 
          registerUser();
        }
    };

    const registerUser = async () => {
        try {
            const response = await axios.post("http://localhost:8081/user/login", {
                email: userData.email,
                password: userData.password
            });
            localStorage.setItem("token", response.data.token);

            sessionStorage.setItem('user', JSON.stringify(response.data));

            dispatch(userLogin({
                id: response.data.id,
                username: response.data.fullName,
                email: response.data.email,
                connected: false,
                token: response.data.token,
            }))


            setTimeout(() => {
                navigate('/chatRoom');
            }, 150);
        } catch (error) {
            setErrorMessage(true);
            console.error("Login failed:", error);
        }


    };

    return (
        <div className='backgroud_login'>
            <div className='register'>
                <img src={logoLK} />
                <h1 style={{ "textAlign": "center" }}>Bem Vindo!</h1>
                <form>
                    {erroMessage === true ?
                        <h5 style={{ color: "red" }}>Falha no Login. Verifique suas credenciais e tente novamente.</h5> :
                        <h2></h2>
                    }
                    <input
                        id='email'
                        className="input-login"
                        type='email'
                        placeholder='Email'
                        value={userData.email}
                        onChange={(e) => setUserData((prevState) => ({
                            ...prevState,
                            email: e.target.value
                        }))}
                    />
                    <div style={{ "display": "flex", "alignItems": "center" }}>
                        <input
                            id='password'
                            type={showPassword ? 'text' : 'password'}
                            className="input-login"
                            style={{ marginTop: "10px" }}
                            placeholder='Senha'
                            value={userData.password}
                            onKeyDown={handleKeyDown}
                            onChange={(e) => {
                                setUserData((prevState) => ({
                                    ...prevState,
                                    password: e.target.value
                                }));
                                setErrorMessage(false);
                            }}
                        />
                        <button
                            type="button"
                            onClick={togglePasswordVisibility}
                            className="send-button"
                        >
                            {showPassword ?
                                <img src={NoVisibleIcon} />
                                : <img src={VisibleIcon} />}

                        </button>
                    </div>
                    <button className="button-login" type='button' onClick={registerUser}>Login</button>
                    <Link style={{ display: "block", textAlign: "center", marginTop: "10px" }}
                        to="/cadastro">
                        Não tem uma conta? Cadastre-se
                    </Link>
                </form>
            </div>

        </div>
    );
}
export default login;