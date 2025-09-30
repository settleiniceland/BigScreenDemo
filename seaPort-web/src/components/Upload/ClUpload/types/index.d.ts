export declare namespace Upload {
	interface Rule {
		name: string;
		type: string;
		color: string;
		exts: string[];
	}

	interface Item {
    url?: string;
    uid?: string;
    progress?: number;
    preload?: string;
    error?: string;
    isPlay?: boolean;
    uploading?: boolean;
    compressing?: boolean;
    [key: string]: any;
  }

	interface Options {
		prefixPath?: string;
		onProgress?(progress: number): void;
		[key: string]: any;
	}

	type Respose = Promise<{
		key: string;
		url: string;
    fileId: string;
    name: string;
    [key: string]: any;
	}>;

	interface Request {
		host: string;
		preview?: string;
		data?: any;
	}
}
