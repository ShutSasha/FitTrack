import { Injectable } from '@nestjs/common'
import * as nodemailer from 'nodemailer'
import { SendConfirmEmailDto } from '~types/email.types'

@Injectable()
export class EmailService {
  private transporter: nodemailer.Transporter

  constructor() {
    this.transporter = nodemailer.createTransport({
      host: process.env.SMTP_HOST,
      port: +process.env.SMTP_PORT,
      secure: false,
      auth: {
        user: process.env.SMTP_USER,
        pass: process.env.SMTP_PASSWORD,
      },
    })
  }

  async sendConfirmationEmail(dto: SendConfirmEmailDto) {
    const confirmationUrl = `${
      process.env.NODE_ENV === 'production' ? process.env.APP_URL : 'http://localhost:5000/api'
    }/auth/confirm-email/${dto.token}`

    await this.transporter.sendMail({
      from: `"Your App" <${process.env.SMTP_USER}>`,
      to: dto.email,
      subject: 'Confirm Your Email',
      html: `
      <div style="background-color:#121212;padding:40px;color:#ffffff;font-family:Arial,sans-serif;border-radius:10px;max-width:500px;margin:auto;">
        <h2 style="color:#00BFFF;text-align:center;">Confirm Your Email</h2>
        <p style="font-size:16px;text-align:center;">
          Thank you for registering! To complete your registration, please confirm your email by clicking the button below:
        </p>
        <div style="text-align:center;margin:30px 0;">
          <a href="${confirmationUrl}" style="background-color:#00BFFF;color:#ffffff;padding:14px 28px;border-radius:6px;text-decoration:none;font-weight:bold;display:inline-block;">
            Confirm Email
          </a>
        </div>
        <p style="font-size:14px;color:#aaaaaa;text-align:center;">
          Or use this link if the button doesn’t work:
        </p>
        <p style="font-size:12px;word-break:break-all;color:#888888;text-align:center;">
          <a href="${confirmationUrl}" style="color:#888888;">${confirmationUrl}</a>
        </p>
      </div>
    `,
    })
  }

  async sendConfirmationResetPasswordCode(email: string, code: number) {
    await this.transporter.sendMail({
      from: `"Your App" <${process.env.SMTP_USER}>`,
      to: email,
      subject: 'Reset your password',
      html: `
      <div style="background-color:#121212;padding:40px;color:#ffffff;font-family:Arial,sans-serif;border-radius:10px;max-width:500px;margin:auto;">
        <h2 style="color:#00BFFF;text-align:center;">Password Reset Code</h2>
        <p style="font-size:16px;text-align:center;color:#ffffff">
          You have requested to reset your password. Use the code below to proceed:
        </p>
        <div style="margin:30px auto;text-align:center;">
          <span style="display:inline-block;font-size:32px;letter-spacing:12px;font-weight:bold;color:#ffffff;background:#1e1e1e;padding:15px 20px;border-radius:8px;">
            ${code.toString().split('').join(' ')}
          </span>
        </div>
        <p style="font-size:14px;color:#aaaaaa;text-align:center;">
          If you didn’t request this, you can safely ignore this email.
        </p>
      </div>
    `,
    })
  }
}
