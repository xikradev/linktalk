import axios from 'axios';

const apiLinkTalk = axios.create({
    baseURL: 'http://localhost:8081', // URL base da sua API
    headers: {
        'Content-Type': 'application/json', // Define o tipo de conteúdo como JSON
    },
});

// Configura o interceptador para adicionar o token JWT em cada requisição
apiLinkTalk.interceptors.request.use(config => {
    // Recupera o token do sessionStorage
    const user = JSON.parse(sessionStorage.getItem('user'));

    // Adiciona o token no cabeçalho Authorization se estiver presente
    if (user) {
        config.headers.Authorization = `${user.token}`;
    }

    return config;
}, error => {
    return Promise.reject(error);
});

export default apiLinkTalk;