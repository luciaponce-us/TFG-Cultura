export interface ApiError {
  timestamp: string;
  status: number;
  errors?: Record<string, string>;
  message: string;
}

export class ApiException extends Error {
  public readonly status: number;
  public readonly errors?: Record<string, string>;
  public readonly timestamp?: string;

  constructor(
    status: number,
    message: string,
    errors?: Record<string, string>,
    timestamp?: string,
  ) {
    super(message);
    this.status = status;
    this.errors = errors;
    this.timestamp = timestamp;
    this.name = "ApiException";
  }
}

export interface Paginated<T> {
  totalElements: number;
  totalPages: number;
  pageable: {
    paged: boolean;
    pageNumber: number;
    pageSize: number;
    unpaged: boolean;
    offset: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
  };
  first: boolean;
  last: boolean;
  size: number;
  content: T[];
  number: number;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}
