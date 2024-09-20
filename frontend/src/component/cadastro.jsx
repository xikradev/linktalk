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
    const [erroMessage, setErrorMessage] = useState(false);
    const [errorEmail, setErrorEmail] = useState(false);
    const navigate = []
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
            const emailExist = await axios.get("http://localhost:8081/user/verify-email/" + userData.email);

            if (emailExist.data) {
                setErrorEmail(true);
                return;
            } else {
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
            }
        } catch (error) {
            console.error("Registration failed:", error);
            setErrorMessage(true);
        }
    };

    return (
        <div className='backgroud_login'>
            <div className='register'>
                <img src={logoLK} />
                <h1 style={{ "textAlign": "center" }}>Cadastre-se</h1>
                <form>
                {errorEmail === true ?
                        <h5 style={{ color: "red" }}>
                        Email já cadastrado.</h5> :
                        <h2></h2>
                    }
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
                        onChange={(e) => {
                            setUserData((prevState) => ({
                            ...prevState,
                            email: e.target.value
                            }));
                            setErrorEmail(false);
                        }}
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
                    {erroMessage === true ?
                        <h5 style={{ color: "red" }}>
                        Falha no Cadastro. 
                        A senha deve conter 8 dígitos, 1 caracter especial, Minimo um maiúsculo e um minúsculo.</h5> :
                        <h2></h2>
                    }
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

