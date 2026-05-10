export interface ApiError {
  timestamp: string; // LocalDateTime → ISO string
  status: number;
  error: string;
  message: string;
}
