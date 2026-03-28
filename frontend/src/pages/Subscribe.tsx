import { type JSX, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ApiKeySubmit from "@/features/subscribe/components/ApiKeySubmit";
import "@styles/pages/Subscribe.css";
import { useRegistration } from "@/context/RegistrationContext";
import { Toaster } from "react-hot-toast";

const SubscribeFlow = () => {
  const { registrationData } = useRegistration();
  const navigate = useNavigate();

  useEffect(() => {
    if (registrationData?.apiKey) {
      if (window.location.pathname !== "/login") {
        navigate("/login", { replace: true });
      }
    }
  }, [registrationData?.apiKey, navigate]);

  const handleKeyValidated = () => {
    if (window.location.pathname !== "/") {
      navigate("/", { replace: true });
    }
  };

  return <ApiKeySubmit onNext={handleKeyValidated} />;
};

const Subscribe = (): JSX.Element => {
  return (
    <>
      <Toaster position="top-center" reverseOrder={false} />
      <div className="subscribe">
        <SubscribeFlow />
      </div>
    </>
  );
};

export default Subscribe;
