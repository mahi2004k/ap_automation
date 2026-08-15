import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const [accessToken, setAccessToken] = useState(
        localStorage.getItem("accessToken")
    );

    const login = (accessToken, refreshToken) => {

        localStorage.setItem(
            "accessToken",
            accessToken
        );

        localStorage.setItem(
            "refreshToken",
            refreshToken
        );

        setAccessToken(accessToken);
    };

    const logout = () => {

        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");

        setAccessToken(null);
    };

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                login,
                logout,
                isAuthenticated: !!accessToken
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}