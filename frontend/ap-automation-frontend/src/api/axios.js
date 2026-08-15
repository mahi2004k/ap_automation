import axios from "axios";

const api = axios.create({
    baseURL:
        import.meta.env.VITE_API_BASE_URL ||
        "http://localhost:8080",

    headers: {
        "Content-Type": "application/json"
    }
});

// Attach access token to every request
api.interceptors.request.use(
    (config) => {

        const accessToken =
            localStorage.getItem("accessToken");

        if (accessToken) {
            config.headers.Authorization =
                `Bearer ${accessToken}`;
        }

        return config;
    },

    (error) => Promise.reject(error)
);


// Automatically refresh expired access token
api.interceptors.response.use(
    (response) => response,

    async (error) => {

        const originalRequest = error.config;

        if (
            error.response?.status === 401 &&
            !originalRequest._retry
        ) {

            originalRequest._retry = true;

            try {

                const refreshToken =
                    localStorage.getItem("refreshToken");

                // No refresh token → login again
                if (!refreshToken) {
                    throw new Error("Refresh token not found");
                }

                const response = await axios.post(
                    `${import.meta.env.VITE_API_BASE_URL}/api/auth/refresh`,
                    {
                        refreshToken: refreshToken
                    }
                );

                const newAccessToken =
                    response.data.accessToken;

                // Save new access token
                localStorage.setItem(
                    "accessToken",
                    newAccessToken
                );

                // Update failed request
                originalRequest.headers.Authorization =
                    `Bearer ${newAccessToken}`;

                // Retry original request
                return api(originalRequest);

            } catch (refreshError) {

                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");

                window.location.href = "/login";

                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;