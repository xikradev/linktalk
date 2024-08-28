import React, { useState } from 'react'
import {over} from 'stompjs';
import SockJS from 'sockjs-client';

var stompClient = null;
const chatRoom = () => {
    const [publicChat, setPublicChat] = useState([]);
    const [userData, setUserData] = useState({
        username: '',
        receptorname:'',
        message: '',
        connected: false,
    });

    const handleUserName = (event) => {
        const value = event.target.value;
        setUserData({
            ...userData,
            username: value,
        });
    };

    const registerUser = () => {
        let Sock= new SockJS('http://localhost:8080/websocket');
        stompClient = over(Sock);
        stompClient.connect({}, onConnected, onError);
    };

    const onConnected = () => {
        setUserData({
            ...userData,
            connected: true,
        });
        stompClient.subscribe('/chatroom/public', onPublicMessageReceived);
        stompClient.subscribe('/user/'+userData.username+'/private', onPrivateMessageReceived)
    }

    const onError = (err) =>{
        console.log(err);
    }

    const onPublicMessageReceived = (payLoad) => {
        let payLoadData = JSON.parse(payLoad.body);
        switch(payLoadData.status){
            case "JOIN": 
                break;
            case 'MESSAGE':
                publicChat.push(payLoadData);
                setPublicChat([...publicChat]);
                break;
            default:
                break; 
        }
    }

    

  return (
    <div className='container'>
        {userData.connected? 
        <div>
        </div>
        :
        <div className='register'>
            <input
            id='username'
            placeholder='Nome de usuário'
            value={userData.username}
            onChange={(e) => setUserData(handleUserName)}
            />
            <button type='button' onClick={registerUser}>Registrar</button>
        </div>
    }
    </div>
  )
}

export default chatRoom