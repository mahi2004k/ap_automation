import { useState, useEffect } from "react";
import { loginUser } from "../../api/authApi";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../../context/AuthContext";


function Login() {

    const navigate = useNavigate();

    const { login, isAuthenticated } = useAuth();


    const [form, setForm] = useState({

        email: "",
        password: ""

    });


    useEffect(() => {

        if (isAuthenticated) {

            navigate("/dashboard", { replace: true });

        }

    }, [isAuthenticated, navigate]);



    const handleChange = (e) => {

        setForm({

            ...form,
            [e.target.name]: e.target.value

        });

    };



    const handleSubmit = async (e) => {

        e.preventDefault();


        try {


            const response = await loginUser(form);


            // Store authentication data through Context

            login(
    response.data.accessToken,
    response.data.refreshToken
);


            toast.success(
                response.data.message || "Login successful"
            );


        } catch (error) {


            toast.error(

                error.response?.data?.message ||
                "Login Failed"

            );


        }

    };



    return (

        <div className="auth-container">

            <div className="card auth-card p-4">


                <div className="text-center mb-4">


                    <div className="logo">

                        AP Automation

                    </div>


                    <small>

                        Sign in to continue

                    </small>


                </div>



                <form onSubmit={handleSubmit}>


                    <div className="mb-3">

                        <label>Email</label>


                        <input

                            type="email"

                            name="email"

                            value={form.email}

                            autoComplete="email"

                            className="form-control"

                            onChange={handleChange}

                            required

                        />


                    </div>




                    <div className="mb-3">


                        <label>Password</label>


                        <input

                            type="password"

                            name="password"

                            value={form.password}

                            autoComplete="current-password"

                            className="form-control"

                            onChange={handleChange}

                            required

                        />


                    </div>




                    <button

                        className="btn btn-primary w-100"

                    >

                        Login

                    </button>



                </form>




                <div className="text-center mt-3">


                    Don't have an account?


                    <Link to="/register">

                        Register

                    </Link>


                </div>



            </div>


        </div>

    );

}


export default Login;