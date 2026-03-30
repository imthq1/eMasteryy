import {
  createContext,
  useContext,
  type ReactNode,
  useCallback,
  useMemo,
  useEffect,
} from "react";
import { useLocalStorage } from "../hooks/useLocalStorage";
import { getAccountService } from "@/features/auth/service/authService";
import toast from "react-hot-toast";

interface RegistrationData {
  apiKey?: string;
  fullName?: string;
  age?: string;
  gender?: string;
  level?: string;
}

interface RegistrationContextType {
  registrationData: RegistrationData;
  updateRegistrationData: (newData: Partial<RegistrationData>) => void;
}

const RegistrationContext = createContext<RegistrationContextType | undefined>(
  undefined,
);

const initialData: RegistrationData = {};

export const RegistrationProvider = ({ children }: { children: ReactNode }) => {
  const [storedData, setStoredData] = useLocalStorage<RegistrationData>(
    "userRegistrationData",
    initialData,
  );
  const fetchProfile = useCallback(async () => {
    try {
      const data = await getAccountService();

      const userData = {
        fullName: data.FullName || "",
        age: data.Age ? data.Age.toString() : "",
        gender: data.Gender ? data.Gender.toLowerCase() : "",
        level: data.englishLevel || "",
      };

      updateRegistrationData(userData);
      console.log("api", data);
    } catch (err) {
      toast.error("Không tải được thông tin user");
    }
  }, []);
  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);
  const updateRegistrationData = useCallback(
    (newData: Partial<RegistrationData>) => {
      setStoredData((prevData) => ({ ...prevData, ...newData }));
    },
    [setStoredData],
  );

  const value = useMemo(
    () => ({
      registrationData: storedData,
      updateRegistrationData,
    }),
    [storedData, updateRegistrationData],
  );

  return (
    <RegistrationContext.Provider value={value}>
      {children}
    </RegistrationContext.Provider>
  );
};

export const useRegistration = (): RegistrationContextType => {
  const context = useContext(RegistrationContext);
  if (context === undefined) {
    throw new Error(
      "useRegistration must be used within a RegistrationProvider",
    );
  }
  return context;
};
