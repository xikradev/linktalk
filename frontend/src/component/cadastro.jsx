import { useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import logoLK from '/img/linktalk.png';
import VisibleIcon from '/img/olho.png';
import NoVisibleIcon from '/img/naoVisivel.png';

const Cadastro = () => {
    const [userData, setUserData] = useState({
        username: '',
        email: '',
        password: '',
    });
    const [showPassword, setShowPassword] = useState(false);

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword); // Alterna entre mostrar e esconder a senha
    };

    const registerUser = async () => {
        try {
            const response = await axios.post("http://localhost:8081/user/register", {
                fullName: userData.username,
                email: userData.email,
                password: userData.password
            });

            console.log("Registration response:", response.data);
            alert("Registration successful!");
            const data = response.data;
            setTimeout(() => {
                navigate('/login');
            }, 150);
        } catch (error) {
            console.error("Registration failed:", error);
            alert("Failed to register. Please check your details and try again.");
        }
    };

    return (
        <div className='backgroud_login'>
            <div className='register'>
                <img src={logoLK} />
                <h1 style={{ "textAlign": "center" }}>Cadastre-se</h1>
                <form>
                    <input
                        id='username'
                        className="input-login"
                        type='text'
                        placeholder='Nome Completo'
                        value={userData.username}
                        onChange={(e) => setUserData((prevState) => ({
                            ...prevState,
                            username: e.target.value
                        }))}
                    />
                    <input
                        id='email'
                        className="input-login"
                        style={{ marginTop: "10px" }}
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
                            onChange={(e) => setUserData((prevState) => ({
                                ...prevState,
                                password: e.target.value
                            }))}
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
                    <button className="button-login" type='button' onClick={registerUser}>Registrar</button>
                    <Link style={{ display: "block", textAlign: "center", marginTop: "10px" }}
                        to="/login">
                        Já tem uma conta? Faça login</Link>
                </form >
            </div >
        </div >
    );
}
export default Cadastro;

