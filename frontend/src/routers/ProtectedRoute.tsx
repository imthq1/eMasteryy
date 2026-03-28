import { useState, useEffect, type ReactNode, type JSX } from "react";
import { Navigate } from "react-router-dom";
import { useRegistration } from "@/context/RegistrationContext";
import { useApiKeyCheck } from "@features/subscribe/hooks/useApiKeyCheck";
import Loader from "@/components/common/Loader";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps): JSX.Element => {
  const [isVerifying, setIsVerifying] = useState(true);
  const [isAllowed, setIsAllowed] = useState(false);

  const [redirectPath, setRedirectPath] = useState("/login");

  const { registrationData, updateRegistrationData } = useRegistration();
  const { validateApiKey } = useApiKeyCheck();

  useEffect(() => {
    const verifyAccess = async () => {
      const accessToken = localStorage.getItem("access_token");
      if (!accessToken) {
        setRedirectPath("/login");
        setIsAllowed(false);
        setIsVerifying(false);
        return;
      }

      const { apiKey } = registrationData;
      if (!apiKey) {
        setRedirectPath("/subscribe"); 
        setIsAllowed(false);
        setIsVerifying(false);
        return;
      }

      const isKeyValid = await validateApiKey(apiKey);
      if (isKeyValid) {
        setIsAllowed(true);
      } else {
        updateRegistrationData({ apiKey: undefined });
        setRedirectPath("/subscribe");
        setIsAllowed(false);
      }

      setIsVerifying(false);
    };

    verifyAccess();
  }, [registrationData?.apiKey, validateApiKey, updateRegistrationData]);

  if (isVerifying) {
    return <Loader />;
  }

  return isAllowed ? <>{children}</> : <Navigate to={redirectPath} replace />;
};

export default ProtectedRoute;
