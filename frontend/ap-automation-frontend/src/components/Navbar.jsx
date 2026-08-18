import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {

    const navigate = useNavigate();

    const { logout } = useAuth();


    const handleLogout = () => {

        logout();

        navigate("/login", { replace: true });

    };


    return (

        <nav
            className="navbar navbar-dark bg-primary px-4"
            style={{
                position: "fixed",
                width: "100%",
                top: 0,
                zIndex: 1000
            }}
        >

            <span className="navbar-brand">
                AP Automation
            </span>


            <button
                className="btn btn-light"
                onClick={handleLogout}
            >

                Logout

            </button>

        </nav>

    );

}

export default Navbar;