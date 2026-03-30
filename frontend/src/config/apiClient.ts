const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const apiClient = async (
  endpoint: string,
  method: string = "GET",
  data?: any,
) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const token = localStorage.getItem("access_token");

  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: data ? JSON.stringify(data) : undefined,
    });

    if (!response.ok) {
      let errorMessage = "API Error";
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorMessage;
      } catch {
        errorMessage = response.statusText;
      }
      throw new Error(errorMessage);
    }

    return await response.json();
  } catch (error) {
    console.error(`API Error at ${endpoint}:`, error);
    throw error;
  }
};
