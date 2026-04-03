const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const fetchDummyData = async (): Promise<string> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/dummy`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.text();
    return data;
  } catch (error) {
    console.error("Error fetching dummy data:", error);
    throw error;
  }
};

export const fetchMongoData = async (): Promise<string> => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/dummy/mongodb`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.text();
    return data;
  } catch (error) {
    console.error("Error fetching MongoDB data:", error);
    throw error;
  }
};
